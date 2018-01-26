package com.prettymuchbryce.abidecoder 

import org.web3j.protocol.Web3j
import org.web3j.abi.Utils;
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.protocol.http.HttpService
import com.fasterxml.jackson.module.kotlin.*
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeDecoder
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.AbiTypes
import java.util.stream.Collectors
import org.web3j.utils.Numeric
import org.web3j.crypto.Hash

fun main(args: Array<String>) {
	var web3: Web3j = Web3j.build(HttpService())
	println("Hello, world!")
	println(web3)

	val json = """
			[{"inputs": [{"type": "address", "name": ""}], "constant": true, "name": "isInstantiation", "payable": false, "outputs": [{"type": "bool", "name": ""}], "type": "function"}, {"inputs": [{"type": "address[]", "name": "_owners"}, {"type": "uint256", "name": "_required"}, {"type": "uint256", "name": "_dailyLimit"}], "constant": false, "name": "create", "payable": false, "outputs": [{"type": "address", "name": "wallet"}], "type": "function"}, {"inputs": [{"type": "address", "name": ""}, {"type": "uint256", "name": ""}], "constant": true, "name": "instantiations", "payable": false, "outputs": [{"type": "address", "name": ""}], "type": "function"}, {"inputs": [{"type": "address", "name": "creator"}], "constant": true, "name": "getInstantiationCount", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"indexed": false, "type": "address", "name": "sender"}, {"indexed": false, "type": "address", "name": "instantiation"}], "type": "event", "name": "ContractInstantiation", "anonymous": false}]
		"""

	val mapper = jacksonObjectMapper()

	var contracts: List<AbiDefinition> = mapper.readValue<List<AbiDefinition>>(json)
	println(contracts[0].getName())
}

// fun decodeParams(types: List<String>, 

/*
SolidityCoder.prototype.decodeParams = function (types, bytes) {
    var solidityTypes = this.getSolidityTypes(types);
    var offsets = this.getOffsets(types, solidityTypes);

    return solidityTypes.map(function (solidityType, index) {
        return solidityType.decode(bytes, offsets[index],  types[index], index);
    });
};
*/

data class DecodedMethod(
	val name: String,
	val params: List<Param>
);

class Param constructor(namedType: AbiDefinition.NamedType, value: Type<*>) {
	val _value = value
	val _namedType = namedType

	fun getValue(): Any {
		return _value.getValue()
	}

	fun getName(): String {
		return _namedType.getName()
	}

	fun getType(): String {
		return _namedType.getType()
	}
}

class Decoder {
	val _savedAbis = mutableListOf<AbiDefinition>()
	val _methodIDs: HashMap<String, AbiDefinition> = HashMap()

	private fun getTypesFromNamedTypes(input: String, namedTypes: List<AbiDefinition.NamedType>): List<Type<*>> {
		val result = mutableListOf<Type<*>>()
		for (namedType in namedTypes) {
			val c = AbiTypes.getType(namedType.getType())
			val type: Type<*> = c.javaClass()
			result.append(type)
		}
		return result
	}
	
	private fun buildMethodSignature(methodName: String, parameters: List<AbiDefinition.NamedType>): String {
        val result = StringBuilder()
        result.append(methodName);
        result.append("(");
        val params = parameters.stream()
                .map{t -> t.getType()}
                .collect(Collectors.joining(","));
        result.append(params);
        result.append(")");
		val bytes = result.toString().toByteArray()
		val hash = Hash.sha3(bytes)
		return Numeric.toHexString(hash)
	}

    private fun buildMethodId(methodSignature: String): String {
        val input = methodSignature.toByteArray();
        val hash = Hash.sha3(input);
        return Numeric.toHexString(hash).substring(0, 10);
    }

	fun addAbi (abis: List<AbiDefinition>) {
		for (abi in abis) {
			if (abi.getName() != null) {
				val methodSignature = buildMethodSignature(abi.getName(), abi.getInputs())
				if (abi.getType() == "event") {
					_methodIDs[methodSignature.substring(0, 2)] = abi
				} else {
					_methodIDs[methodSignature.substring(2, 10)] = abi
				}
			}
		}

		_savedAbis.addAll(abis)
	}

	fun getAbis(): List<AbiDefinition> {
		return _savedAbis
	}

	fun getMethodIDs(): Map<String, AbiDefinition> {
		return _methodIDs	
	}

	fun decodeMethod(data: String): DecodedMethod {
		val methodID = data.substring(2, 10)
		val abiItem = _methodIDs[methodID]
		if (abiItem != null) {
			val params = abiItem.inputs
			val decoded = FunctionReturnDecoder.decode(data, params)
		}
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
