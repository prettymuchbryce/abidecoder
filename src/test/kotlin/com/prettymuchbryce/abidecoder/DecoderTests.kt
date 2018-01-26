package com.prettymuchbryce.abidecoder

import org.junit.Assert.*
import com.fasterxml.jackson.module.kotlin.*
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.junit.Test

val testAbiJson= """
			[{"inputs": [{"type": "address", "name": ""}], "constant": true, "name": "isInstantiation", "payable": false, "outputs": [{"type": "bool", "name": ""}], "type": "function"}, {"inputs": [{"type": "address[]", "name": "_owners"}, {"type": "uint256", "name": "_required"}, {"type": "uint256", "name": "_dailyLimit"}], "constant": false, "name": "create", "payable": false, "outputs": [{"type": "address", "name": "wallet"}], "type": "function"}, {"inputs": [{"type": "address", "name": ""}, {"type": "uint256", "name": ""}], "constant": true, "name": "instantiations", "payable": false, "outputs": [{"type": "address", "name": ""}], "type": "function"}, {"inputs": [{"type": "address", "name": "creator"}], "constant": true, "name": "getInstantiationCount", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"indexed": false, "type": "address", "name": "sender"}, {"indexed": false, "type": "address", "name": "instantiation"}], "type": "event", "name": "ContractInstantiation", "anonymous": false}]
		"""

val mapper = jacksonObjectMapper()
var contracts: List<AbiDefinition> = mapper.readValue<List<AbiDefinition>>(testAbiJson)

class DecoderTests {
	@Test
	fun addAbis() {
		val decoder = Decoder()
		decoder.addAbi(contracts)
		val abis = decoder.getAbis()
		assertEquals(5, abis.size)
		val methodIDs = decoder.getMethodIDs()
		assertEquals(5, methodIDs.size)
	}
}
