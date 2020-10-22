package com.example.videoStreaming

import ConvertM3U8
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receiveText
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import java.io.File
import java.util.*

object VideoStream {
 interface AuthnRequest{
     fun request(call:ApplicationCall):Boolean
 }
    fun run(route: Routing,getRequest:AuthnRequest,postRequest:AuthnRequest,ffmpeg:String="/usr/local/bin/ffmpeg") {
        File("HLS_ROUT").mkdir()
        File("Video_Convert_Root").mkdir()
        route{
            for(i in File("HLS_ROUT").listFiles()){
                if(i.isDirectory){
                    for(a in i.listFiles()){
                        get("VideoStream/${i.name}/${a.name}"){
                            if(getRequest.request(call)){
                               call.respondFile(File("HLS_ROUT/${i.name}/${a.name}"))
                            }else{
                                call.respondText("Verification Failed")
                            }
                        }
                        get("VideoFrame/${i.name}/${a.name}"){
                            if(getRequest.request(call)){
                                call.respondText(
                                    frameText.replace("%fileName%","${i.name}/${a.name}"),ContentType.Text.Html
                                )
                            }else{
                                call.respondText("Verification Failed")
                            }
                        }
                    }
                }
            }
            post("UploadVideo"){
                if(postRequest.request(call)){
                    val map:MutableMap<String,Any> = Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                    val returnMap:MutableMap<String,Any> = mutableMapOf()
                    returnMap["result"]=ConvertM3U8.convertOss(Base64.getDecoder().decode(map["data"]!!.toString()),map["fileName"]!!.toString())
                    if(returnMap["result"]==true){
                        route{
                            for(a in File("HLS_ROUT/${map["fileName"]!!}").listFiles()){
                                get("VideoStream/${map["fileName"]}/${a.name}"){
                                    if(getRequest.request(call)){
                                        call.respondFile(File("HLS_ROUT/${map["fileName"]}/${a.name}"))
                                    }else{
                                        call.respondText("Verification Failed")
                                    }
                                }
                                get("VideoFrame/${map["fileName"]}/${a.name}"){
                                    if(getRequest.request(call)){
                                        call.respondText(
                                            frameText.replace("%fileName%","${map["fileName"]}/${a.name}"),ContentType.Text.Html
                                        )
                                    }else{
                                        call.respondText("Verification Failed")
                                    }
                                }
                            }
                        }
                    }
                    call.respondText(Gson().toJson(returnMap), ContentType.Text.Plain)
                }else{
                    call.respondText("Verification Failed")
                }

            }

        }
    }

    val frameText  = File("resources/VideoFrame.html").readText()
}

