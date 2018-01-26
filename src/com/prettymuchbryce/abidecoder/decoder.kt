package abidecoder

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.protocol.http.HttpService
import com.fasterxml.jackson.module.kotlin.*
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Type
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

fun decodeParams(types: List<String>, 

/*
SolidityCoder.prototype.decodeParams = function (types, bytes) {
    var solidityTypes = this.getSolidityTypes(types);
    var offsets = this.getOffsets(types, solidityTypes);

    return solidityTypes.map(function (solidityType, index) {
        return solidityType.decode(bytes, offsets[index],  types[index], index);
    });
};
*/



class Decoder constructor() {
	val _savedAbis = mutableListOf<AbiDefinition>()
	val _methodIDs: HashMap<String, AbiDefinition> = HashMap()
	
	/*
	fun getAbis(): List<AbiDefinition> {
	}*/

	fun buildMethodSignature(methodName: String, parameters: List<AbiDefinition.NamedType>): String {
        val result = StringBuilder()
        result.append(methodName);
        result.append("(");
        val params = parameters.stream()
                .map(AbiDefinition.NamedType::getTypeAsString)
                .collect(Collectors.joining(","));
        result.append(params);
        result.append(")");
        return result.toString();
	}

    fun buildMethodId(methodSignature: String): String {
        val input = methodSignature.toByteArray();
        val hash = Hash.sha3(input);
        return Numeric.toHexString(hash).substring(0, 10);
    }

	public fun addAbi (abis: List<AbiDefinition>) {
		for (abi in abis) {
			if (abi.getName() != null) {
				val methodSignature = buildMethodSignature(abi.getName(), abi.getInputs())
				if (abi.getType() == "event") {
					_methodIDs[methodSignature.slice(2)] = abi
				} else {
					_methodIDs[methodSignature.slice(2, 10)] = abi
				}
			}
		}
		abis[0].	
		abis.
	}

	public fun getMethodIDs(): Map<String, AbiDefinition> {
		return _methodIDs	
	}

	fun decodeMethod(data: String) { }
	fun decodeLogs() { }
	fun removeABI() { }
}

