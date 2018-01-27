package com.prettymuchbryce.abidecoder 

import com.fasterxml.jackson.module.kotlin.*
import org.spongycastle.util.encoders.Hex
import org.ethereum.vm.trace.Serializers

/*
import org.web3j.abi.Utils;
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeDecoder
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.AbiTypes
*/

import org.ethereum.solidity.Abi

import java.util.stream.Collectors

fun main(args: Array<String>) {
	val json = """
			[{"inputs": [{"type": "address", "name": ""}], "constant": true, "name": "isInstantiation", "payable": false, "outputs": [{"type": "bool", "name": ""}], "type": "function"}, {"inputs": [{"type": "address[]", "name": "_owners"}, {"type": "uint256", "name": "_required"}, {"type": "uint256", "name": "_dailyLimit"}], "constant": false, "name": "create", "payable": false, "outputs": [{"type": "address", "name": "wallet"}], "type": "function"}, {"inputs": [{"type": "address", "name": ""}, {"type": "uint256", "name": ""}], "constant": true, "name": "instantiations", "payable": false, "outputs": [{"type": "address", "name": ""}], "type": "function"}, {"inputs": [{"type": "address", "name": "creator"}], "constant": true, "name": "getInstantiationCount", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"indexed": false, "type": "address", "name": "sender"}, {"indexed": false, "type": "address", "name": "instantiation"}], "type": "event", "name": "ContractInstantiation", "anonymous": false}]
		"""
}

class Decoder {
	val _savedAbis = mutableListOf<Abi.Entry>()
	val _methodIDs: HashMap<String, Abi.Entry> = HashMap()

	data class DecodedMethod (
		val name: String,
		val params: List<Param>
	)

	data class Param (
		val name: String,
		val type: String,
		val value: Any
	)

	fun addAbi (json: String) {
		val abi: Abi = Abi.fromJson(json)
		for (entry in abi) {
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
				// val value = Serializers.serializeFieldsOnly(decoded[i]!!, true)
				val value = decoded[i]!!
				val param = Param(name, type, value)
				params.add(param)
			}
			return DecodedMethod(entry.name, params)
		}
		return null
	}
}

/**

    const testData = "0x53d9d9100000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000000000000000000a6d9c5f7d4de3cef51ad3b7235d79ccc95114de5000000000000000000000000a6d9c5f7d4de3cef51ad3b7235d79ccc95114daa";


function _decodeMethod(data) {
  const methodID = data.slice(2, 10);
  const abiItem = state.methodIDs[methodID];
  if (abiItem) {
    const params = abiItem.inputs.map((item) => item.type);
    let decoded = SolidityCoder.decodeParams(params, data.slice(10));
    return {
      name: abiItem.name,
      params: decoded.map((param, index) => {
        let parsedParam = param;
        if (abiItem.inputs[index].type.indexOf("uint") !== -1) {
          parsedParam = new Web3().toBigNumber(param).toString();
        }
        return {
          name: abiItem.inputs[index].name,
          value: parsedParam,
          type: abiItem.inputs[index].type
        };
      })
    }
  }
}



SolidityCoder.prototype.decodeParams = function (types, bytes) {
    var solidityTypes = this.getSolidityTypes(types);
    var offsets = this.getOffsets(types, solidityTypes);

    return solidityTypes.map(function (solidityType, index) {
        return solidityType.decode(bytes, offsets[index],  types[index], index);
    });
};


SolidityCoder.prototype.getOffsets = function (types, solidityTypes) {
    var lengths =  solidityTypes.map(function (solidityType, index) {
        return solidityType.staticPartLength(types[index]);
    });

    for (var i = 1; i < lengths.length; i++) {
         // sum with length of previous element
        lengths[i] += lengths[i - 1];
    }

    return lengths.map(function (length, index) {
        // remove the current length, so the length is sum of previous elements
        var staticPartLength = solidityTypes[index].staticPartLength(types[index]);
        return length - staticPartLength;
    });
};

**/
