package com.prettymuchbryce.abidecoder
import java.math.BigInteger;
import org.junit.Assert.*
import com.fasterxml.jackson.module.kotlin.*
import org.junit.Test
import org.spongycastle.util.encoders.Hex;

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
		assertEquals("_owners", decodedMethod.params[0].name)
		assertEquals("address[]", decodedMethod.params[0].type)
		val mapper = jacksonObjectMapper()
		var jsonStr = mapper.writeValueAsString(decodedMethod.params[0].value)
		val array = decodedMethod.params[0].value
		if (array is Array<*>) {
			if (array[0] is ByteArray) {
				assertEquals("0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114de5", Hex.toHexString(array[0]))
				print("here is it")
				print(decodedMethod.params[0].value)
			}
		}
		// print(jsonStr)
		// print(JSON.stringify(decodedMethod.params[0].value))
		print("WTF")
		// assertEquals("0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114de5", Hex.toHexString(decodedMethod.params[0].value)
	}
}
/*
  it('decode data', () => {
    abiDecoder.addABI(testABI);
    const testData = "0x53d9d9100000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002000000000000000000000000a6d9c5f7d4de3cef51ad3b7235d79ccc95114de5000000000000000000000000a6d9c5f7d4de3cef51ad3b7235d79ccc95114daa";
    const decodedData = abiDecoder.decodeMethod(testData);
    expect(decodedData).to.be.an('object');
    expect(decodedData).to.have.all.keys('name', 'params');
    expect(decodedData.name).to.be.a('string');
    expect(decodedData.params).to.be.a('array');
    expect(decodedData.params).to.have.length.of(3);
    expect(decodedData.params[0].value).to.deep.equal(['0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114de5', '0xa6d9c5f7d4de3cef51ad3b7235d79ccc95114daa']);
    expect(decodedData.params[0].name).to.equal('_owners');
    expect(decodedData.params[0].type).to.equal('address[]');
    expect(decodedData.params[1].value).to.equal('1');
    expect(decodedData.params[1].name).to.equal('_required');
    expect(decodedData.params[1].type).to.equal('uint256');
    expect(decodedData.params[2].value).to.equal('0');
    expect(decodedData.params[2].name).to.equal('_dailyLimit');
    expect(decodedData.params[2].type).to.equal('uint256');
  });*/
