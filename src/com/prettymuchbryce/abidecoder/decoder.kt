package abidecoder

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

fun main(args: Array<String>) {
	var web3: Web3j = Web3j.build(HttpService());
	println("Hello, world!")
	println(web3)
}

class Decoder constructor() {
	fun getABIs() { }
	fun addABI() { }
	fun getMethodIDs() { }
	fun decodeMethod() { }
	fun decodeLogs() { }
	fun removeABI() { }
}
