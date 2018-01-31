package com.prettymuchbryce.abidecoder

import java.math.BigInteger
import org.junit.Assert.*
import com.fasterxml.jackson.module.kotlin.*
import org.junit.Test
import org.spongycastle.util.encoders.Hex

val testAbiJson= """
			[{"inputs": [{"type": "address", "name": ""}], "constant": true, "name": "isInstantiation", "payable": false, "outputs": [{"type": "bool", "name": ""}], "type": "function"}, {"inputs": [{"type": "address[]", "name": "_owners"}, {"type": "uint256", "name": "_required"}, {"type": "uint256", "name": "_dailyLimit"}], "constant": false, "name": "create", "payable": false, "outputs": [{"type": "address", "name": "wallet"}], "type": "function"}, {"inputs": [{"type": "address", "name": ""}, {"type": "uint256", "name": ""}], "constant": true, "name": "instantiations", "payable": false, "outputs": [{"type": "address", "name": ""}], "type": "function"}, {"inputs": [{"type": "address", "name": "creator"}], "constant": true, "name": "getInstantiationCount", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"indexed": false, "type": "address", "name": "sender"}, {"indexed": false, "type": "address", "name": "instantiation"}], "type": "event", "name": "ContractInstantiation", "anonymous": false}]
		"""

class DecoderTests {
	@Test
	fun getAbis() {
		val decoder = Decoder()
		val abis = decoder.getAbis()
		assertEquals(0, abis.size)
	}

	@Test
	fun addAbis() {
		val decoder = Decoder()
		decoder.addAbi(testAbiJson)
		val abis = decoder.getAbis()
		assertEquals(5, abis.size)
		val methodIDs = decoder.getMethodIDs()
		assertEquals(5, methodIDs.size)
	}

	@Test
	fun decodeMethod() {
    	val testData = "0x53d9d9100000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000000000000000000a6d9c5f7d4de3cef51ad3b7235d79ccc95114de5000000000000000000000000a6d9c5f7d4de3cef51ad3b7235d79ccc95114daa";
		val decoder = Decoder()
		decoder.addAbi(testAbiJson)
		val decodedMethod = decoder.decodeMethod(testData)!!
		assertEquals(3, decodedMethod.params.size)
		
		val array = decodedMethod.params[0].value
		if (array is List<*>) {
			assertEquals("0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114de5", "0x" + Hex.toHexString(array[0] as ByteArray))
			assertEquals("0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114daa", "0x" + Hex.toHexString(array[1] as ByteArray))
		}
		assertEquals("_owners", decodedMethod.params[0].name)
		assertEquals("address[]", decodedMethod.params[0].type)

		assertEquals(BigInteger("1"), decodedMethod.params[1].value)
		assertEquals("_required", decodedMethod.params[1].name)
		assertEquals("uint256", decodedMethod.params[1].type)

		assertEquals(BigInteger("0"), decodedMethod.params[2].value)
		assertEquals("_dailyLimit", decodedMethod.params[2].name)
		assertEquals("uint256", decodedMethod.params[2].type)
	}

	@Test
	fun decodeLogsWithoutIndexed() {
		val testLogsJson = """
		[{
        	"data": "0x00000000000000000000000065039084cc6f4773291a6ed7dcf5bc3a2e894ff3000000000000000000000000435a4167bc34107bd03e267f9d6b869255151a27",
        	"topics": ["0x4fb057ad4a26ed17a57957fa69c306f11987596069b89521c511fc9a894e6161"],
        	"address": "0x0457874Bb0a346962128a0C01310d00Fc5bb6a83"
      	}]
		"""
		val decoder = Decoder()
		decoder.addAbi(testAbiJson)
		val decodedLogs = decoder.decodeLogs(testLogsJson)
		assertEquals(1, decodedLogs.size)
		assertEquals("ContractInstantiation", decodedLogs[0].name)
		assertEquals(2, decodedLogs[0].events.size)
		assertEquals("0x0457874Bb0a346962128a0C01310d00Fc5bb6a83", decodedLogs[0].address)

		assertEquals("sender", decodedLogs[0].events[0].name)
		assertEquals("0x65039084cc6f4773291a6ed7dcf5bc3a2e894ff3", "0x" + Hex.toHexString(decodedLogs[0].events[0].value as ByteArray))
		assertEquals("address", decodedLogs[0].events[0].type)

		assertEquals("instantiation", decodedLogs[0].events[1].name)
		assertEquals("0x435a4167bc34107bd03e267f9d6b869255151a27", "0x" + Hex.toHexString(decodedLogs[0].events[1].value as ByteArray))
		assertEquals("address", decodedLogs[0].events[1].type)
	}

	@Test
	fun decodeLogsWithIndexed() {
    	val walletAbiJson = """
			[{"inputs": [{"type": "uint256", "name": ""}], "constant": true, "name": "owners", "payable": false, "outputs": [{"type": "address", "name": ""}], "type": "function"}, {"inputs": [{"type": "address", "name": "owner"}], "constant": false, "name": "removeOwner", "payable": false, "outputs": [], "type": "function"}, {"inputs": [{"type": "uint256", "name": "transactionId"}], "constant": false, "name": "revokeConfirmation", "payable": false, "outputs": [], "type": "function"}, {"inputs": [{"type": "address", "name": ""}], "constant": true, "name": "isOwner", "payable": false, "outputs": [{"type": "bool", "name": ""}], "type": "function"}, {"inputs": [{"type": "uint256", "name": ""}, {"type": "address", "name": ""}], "constant": true, "name": "confirmations", "payable": false, "outputs": [{"type": "bool", "name": ""}], "type": "function"}, {"inputs": [], "constant": true, "name": "calcMaxWithdraw", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"type": "bool", "name": "pending"}, {"type": "bool", "name": "executed"}], "constant": true, "name": "getTransactionCount", "payable": false, "outputs": [{"type": "uint256", "name": "count"}], "type": "function"}, {"inputs": [], "constant": true, "name": "dailyLimit", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [], "constant": true, "name": "lastDay", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"type": "address", "name": "owner"}], "constant": false, "name": "addOwner", "payable": false, "outputs": [], "type": "function"}, {"inputs": [{"type": "uint256", "name": "transactionId"}], "constant": true, "name": "isConfirmed", "payable": false, "outputs": [{"type": "bool", "name": ""}], "type": "function"}, {"inputs": [{"type": "uint256", "name": "transactionId"}], "constant": true, "name": "getConfirmationCount", "payable": false, "outputs": [{"type": "uint256", "name": "count"}], "type": "function"}, {"inputs": [{"type": "uint256", "name": ""}], "constant": true, "name": "transactions", "payable": false, "outputs": [{"type": "address", "name": "destination"}, {"type": "uint256", "name": "value"}, {"type": "bytes", "name": "data"}, {"type": "bool", "name": "executed"}], "type": "function"}, {"inputs": [], "constant": true, "name": "getOwners", "payable": false, "outputs": [{"type": "address[]", "name": ""}], "type": "function"}, {"inputs": [{"type": "uint256", "name": "from"}, {"type": "uint256", "name": "to"}, {"type": "bool", "name": "pending"}, {"type": "bool", "name": "executed"}], "constant": true, "name": "getTransactionIds", "payable": false, "outputs": [{"type": "uint256[]", "name": "_transactionIds"}], "type": "function"}, {"inputs": [{"type": "uint256", "name": "transactionId"}], "constant": true, "name": "getConfirmations", "payable": false, "outputs": [{"type": "address[]", "name": "_confirmations"}], "type": "function"}, {"inputs": [], "constant": true, "name": "transactionCount", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"type": "uint256", "name": "_required"}], "constant": false, "name": "changeRequirement", "payable": false, "outputs": [], "type": "function"}, {"inputs": [{"type": "uint256", "name": "transactionId"}], "constant": false, "name": "confirmTransaction", "payable": false, "outputs": [], "type": "function"}, {"inputs": [{"type": "address", "name": "destination"}, {"type": "uint256", "name": "value"}, {"type": "bytes", "name": "data"}], "constant": false, "name": "submitTransaction", "payable": false, "outputs": [{"type": "uint256", "name": "transactionId"}], "type": "function"}, {"inputs": [{"type": "uint256", "name": "_dailyLimit"}], "constant": false, "name": "changeDailyLimit", "payable": false, "outputs": [], "type": "function"}, {"inputs": [], "constant": true, "name": "MAX_OWNER_COUNT", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [], "constant": true, "name": "required", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"type": "address", "name": "owner"}, {"type": "address", "name": "newOwner"}], "constant": false, "name": "replaceOwner", "payable": false, "outputs": [], "type": "function"}, {"inputs": [{"type": "uint256", "name": "transactionId"}], "constant": false, "name": "executeTransaction", "payable": false, "outputs": [], "type": "function"}, {"inputs": [], "constant": true, "name": "spentToday", "payable": false, "outputs": [{"type": "uint256", "name": ""}], "type": "function"}, {"inputs": [{"type": "address[]", "name": "_owners"}, {"type": "uint256", "name": "_required"}, {"type": "uint256", "name": "_dailyLimit"}], "type": "constructor"}, {"payable": true, "type": "fallback"}, {"inputs": [{"indexed": false, "type": "uint256", "name": "dailyLimit"}], "type": "event", "name": "DailyLimitChange", "anonymous": false}, {"inputs": [{"indexed": true, "type": "address", "name": "sender"}, {"indexed": true, "type": "uint256", "name": "transactionId"}], "type": "event", "name": "Confirmation", "anonymous": false}, {"inputs": [{"indexed": true, "type": "address", "name": "sender"}, {"indexed": true, "type": "uint256", "name": "transactionId"}], "type": "event", "name": "Revocation", "anonymous": false}, {"inputs": [{"indexed": true, "type": "uint256", "name": "transactionId"}], "type": "event", "name": "Submission", "anonymous": false}, {"inputs": [{"indexed": true, "type": "uint256", "name": "transactionId"}], "type": "event", "name": "Execution", "anonymous": false}, {"inputs": [{"indexed": true, "type": "uint256", "name": "transactionId"}], "type": "event", "name": "ExecutionFailure", "anonymous": false}, {"inputs": [{"indexed": true, "type": "address", "name": "sender"}, {"indexed": false, "type": "uint256", "name": "value"}], "type": "event", "name": "Deposit", "anonymous": false}, {"inputs": [{"indexed": true, "type": "address", "name": "owner"}], "type": "event", "name": "OwnerAddition", "anonymous": false}, {"inputs": [{"indexed": true, "type": "address", "name": "owner"}], "type": "event", "name": "OwnerRemoval", "anonymous": false}, {"inputs": [{"indexed": false, "type": "uint256", "name": "required"}], "type": "event", "name": "RequirementChange", "anonymous": false}]
		"""
    
		val testLogsJson = """
		[{
			"data": "0x00000000000000000000000000000000000000000000000000038d7ea4c68000",
			"topics": ["0xe1fffcc4923d04b559f4d29a8bfc6cda04eb5b0d3c460751c2402c5c5cc9109c", "0x00000000000000000000000005039084cc6f4773291a6ed7dcf5bc3a2e894ff3"],
			"address": "0x0457874Bb0a346962128a0C01310d00Fc5bb6a81"
		}]
		"""

		val decoder = Decoder()
		decoder.addAbi(walletAbiJson)
		val decodedLogs = decoder.decodeLogs(testLogsJson)
		
		assertEquals(1, decodedLogs.size)
		assertEquals("Deposit", decodedLogs[0].name)
		assertEquals(2, decodedLogs[0].events.size)
		assertEquals("0x0457874Bb0a346962128a0C01310d00Fc5bb6a81", decodedLogs[0].address)

		assertEquals("sender", decodedLogs[0].events[0].name)
		assertEquals("0x05039084cc6f4773291a6ed7dcf5bc3a2e894ff3", "0x" + Hex.toHexString(decodedLogs[0].events[0].value as ByteArray))
		assertEquals("address", decodedLogs[0].events[0].type)

		assertEquals("value", decodedLogs[0].events[1].name)
		assertEquals(BigInteger("1000000000000000"), decodedLogs[0].events[1].value)
		assertEquals("uint256", decodedLogs[0].events[1].type)
	}

	@Test
	fun removeAbi() {
		val decoder = Decoder()
		decoder.addAbi(testAbiJson)
		var methods = decoder.getMethodIDs()
		assertEquals(5, methods.size)

		decoder.removeAbi(testAbiJson)
		
		methods = decoder.getMethodIDs()
		assertEquals(0, methods.size)
	}
}
