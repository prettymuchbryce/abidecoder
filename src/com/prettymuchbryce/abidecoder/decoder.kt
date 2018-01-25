package abidecoder

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.protocol.http.HttpService
import com.fasterxml.jackson.module.kotlin.*

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

class Decoder constructor() {
	private val _savedAbis = mutableListOf<AbiDefinition>()
	private val _methodIDs: HashMap<String, AbiDefinition> = HashMap()
	
	fun getAbis(): List<AbiDefinition> {
	}

	fun addAbi (abis: List<AbiDefinition>) {
	
	}

	fun getMethodIDs(): Map<String, AbiDefinition> {
		return _methodIDs	
	}

	fun decodeMethod() { }
	fun decodeLogs() { }
	fun removeABI() { }
}

