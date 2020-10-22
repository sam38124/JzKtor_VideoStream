# Server
### You can create the hls video stream very simply by this framework!! 

``` kotlin
 VideoStream.run(this, object :VideoStream.AuthnRequest{
            override fun request(call: ApplicationCall): Boolean {
            //handle your video get event!! 
                return true
            }
        },object :VideoStream.AuthnRequest{
            override fun request(call: ApplicationCall): Boolean {
            //handle your video post event!! 
                return true
            }
        },"/usr/local/bin/ffmpeg")
```

# Client

### 1.Post video
```kotlin
    val data :MutableMap<String,Any> = mutableMapOf()

    data["data"]=Base64.getEncoder().encodeToString(File("/Users/jianzhi.wang/Desktop/test/demo.mov").readBytes())

    data["fileName"]="sam38999"

    val result="http://0.0.0.0:8080/UploadVideo".postRequest(1000*10, Gson().toJson(data))

```

### 2.Get video 

```kotlin
http://0.0.0.0:8080/VideoFrame/sam38999/root.m3u8
```
