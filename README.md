### abidecoder

> A port of [abi-decoder](https://github.com/ConsenSys/abi-decoder) to Kotlin for use with Kotlin/Java projects.

### Installation 

##### adding as a dependency to your Maven project: 

```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
	
<dependency>
	<groupId>org.github.prettymuchbryce</groupId>
	<artifactId>abidecoder</artifactId>
	<version>master-SNAPSHOT</version>
</dependency>
```

##### or your Gradle project: 

```
repositories {
	maven { url 'https://jitpack.io' }
}
compile 'com.github.prettymuchbryce:abidecoder:master-SNAPSHOT'
```

### Usage

```java
package main;

import com.prettymuchbryce.abidecoder.Decoder;

public class Main {
  public static void main(String[] args) {
	Decoder d = new Decoder();
	d.addAbi(myAbiJsonString);
	Decoder.DecodedMethod result = d.decodeMethod(methodData);
	System.out.print(result);
  }
}
```

Please see [unit tests](https://github.com/prettymuchbryce/abidecoder/blob/master/src/test/kotlin/com/prettymuchbryce/abidecoder/DecoderTests.kt) for more usage examples.
