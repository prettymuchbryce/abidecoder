package com.prettymuchbryce.abidecoder 

import com.fasterxml.jackson.module.kotlin.*
import org.spongycastle.util.encoders.Hex
import org.ethereum.vm.LogInfo
import org.ethereum.solidity.Abi

class Decoder {
	val _savedAbis = mutableListOf<Abi.Entry>()
	val _methodIDs: HashMap<String, Abi.Entry> = HashMap()

	data class DecodedMethod (
		val name: String,
		val params: List<Param>
	)

	data class Log (
		val data: String,
		val topics: List<String>,
		val address: String
	)

	data class DecodedLog (
		val name: String,
		val address: String,
		val events: List<Param>
	)

	data class Param (
		val name: String,
		val type: String,
		val value: Any
	)

	fun addAbi (json: String) {
		val abi: Abi = Abi.fromJson(json)
		for (entry in abi) {
			if (entry == null) {
				continue
			}
			if (entry.name != null) {
				val methodSignature = entry.encodeSignature()
				_methodIDs[Hex.toHexString(methodSignature)] = entry
			}
			_savedAbis.add(entry)
		}
	}

	fun getAbis(): List<Abi.Entry> {
		return _savedAbis
	}

	fun getMethodIDs(): Map<String, Abi.Entry> {
		return _methodIDs	
	}

	fun removeAbi (json: String) {
		val abi: Abi = Abi.fromJson(json)
		for (entry in abi) {
			if (entry == null) {
				continue
			}
			if (entry.name != null) {
				val methodSignature = entry.encodeSignature()
				_methodIDs.remove(Hex.toHexString(methodSignature))
			}
			_savedAbis.remove(entry)
		}
	}

	fun decodeMethod(data: String): DecodedMethod? {
		val noPrefix = data.removePrefix("0x")
		val bytes = Hex.decode(noPrefix.toUpperCase())
		val methodBytes: ByteArray = bytes.sliceArray(0..3)
		val entry = _methodIDs[Hex.toHexString(methodBytes)]
		if (entry is Abi.Function) {
			val decoded = entry.decode(bytes)
			val params = mutableListOf<Param>()
			for (i in decoded.indices) {
				val name = entry.inputs[i].name
				val type = entry.inputs[i].type.toString()
				val value = decoded[i]!!
				val param = Param(name, type, value)
				params.add(param)
			}
			return DecodedMethod(entry.name, params)
		}
		return null
	}

	fun padZeros (address: String): String {
	  var formatted = address.removePrefix("0x")

	  if (formatted.length < 40) {
		while (formatted.length < 40) formatted = "0" + formatted;
	  }

	  return "0x" + formatted;
	};

	fun decodeLogs(data: String): List<DecodedLog> {
		val logs: List<Log> = jacksonObjectMapper().readValue(data)
		val result = mutableListOf<DecodedLog>()
		for (log in logs) {
			val noPrefix = log.data.removePrefix("0x")
			val bytes = Hex.decode(noPrefix.toUpperCase())
			val methodID = log.topics[0].removePrefix("0x")
			val methodBytes = Hex.decode(methodID.toUpperCase())
			val entry = _methodIDs[Hex.toHexString(methodBytes)]
			if (entry != null) {
				val nonindexedInputs = mutableListOf<Abi.Entry.Param>()
				for (input in entry.inputs) {
					if (!input.indexed) {
						nonindexedInputs.add(input)
					}
				}
				val decodedParams = mutableListOf<Param>()
				var dataIndex = 0
				var topicsIndex = 1

				for (i in entry.inputs.indices) {
					val input = entry.inputs[i]
					var param: Param? = null
						
					if (input.indexed) {
						val topic = log.topics[topicsIndex].removePrefix("0x")
						val topicBytes = Hex.decode(topic.toUpperCase())
						val decoded = Abi.Entry.Param.decodeList(mutableListOf<Abi.Entry.Param>(input), topicBytes)
						param = Param(input.name, input.type.toString(), decoded[0]!!)
						topicsIndex++
					} else {
						val decoded = Abi.Entry.Param.decodeList(nonindexedInputs, bytes)
						param = Param(input.name, input.type.toString(), decoded[dataIndex]!!)
						dataIndex++
					}
					decodedParams.add(param!!)
				}

				val decodedLog = DecodedLog(entry.name, log.address, decodedParams)
				result.add(decodedLog)	
			}
		}
		return result
	}
}
