/*
 * Haiku Fan Control
*/

preferences {
	input("devname", "text", title: "Device Name", description: "The devices name",required:true)
	input("destIp", "text", title: "IP", description: "The device IP",required:true)
 }

metadata {
	definition (name: "Haiku Fan Control", namespace: "System48/Smartthings/Haiku", author: "System48") {
	capability "Switch"
	capability "Switch Level"
        command "fanon"
        command "fanoff"
        command "setfanlevel"
        command "lighton"
        command "lightoff"
        command "setlightlevel"
        command "sleepon"
        command "sleepoff"
        command "whooshon"
        command "whooshoff"
        
      	}

	simulator {
		// TODO-: define status and reply messages here
	}

	tiles {
    	standardTile("fan", "device.fan", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
            state "fanon", label: 'Fan On', action:"fanoff", backgroundColor: "#ffc425", icon:"st.Lighting.light24"
            state "fanoff", label: 'Fan Off', action:"fanon", backgroundColor: "#ffffff", icon:"st.Lighting.light24"
        }
        controlTile("fanlevel", "device.fanlevel", "slider", height: 1, width: 2, inactiveLabel: false, range: "(0..7)") {
			state "fanlevel", label: '${name}', action:"setfanlevel"
		}
		standardTile("light", "device.light", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
            state "lighton", label: 'Light On', action:"lightoff", backgroundColor: "#ffc425", icon:"st.Lighting.light21"
            state "lightoff", label: 'Light Off', action:"lighton", backgroundColor: "#ffffff", icon:"st.Lighting.light21"
        }
        controlTile("lightlevel", "device.lightlevel", "slider", height: 1, width: 2, inactiveLabel: false, range: "(0..16)") {
			state "lightlevel", label: '${name}', action:"setlightlevel"
		}
       // standardTile("sleep", "device.sleep", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
        //	state "sleepoff", label: 'Sleep Off', action:"sleepon", backgroundColor: "#ffffff", icon:"st.Weather.weather4"
     //       state "sleepon", label: 'Sleep On', action:"sleepoff", backgroundColor: "#ffc425", icon:"st.Weather.weather4"
      //  }
        standardTile("sleep", "device.sleep", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
            state "sleepoff", label: 'Sleep On', action:"sleepon", backgroundColor: "#ffc425", icon:"st.Weather.weather4"
            state "sleepoff", label: 'Sleep On', action:"sleepon", backgroundColor: "#ffc425", icon:"st.Weather.weather4"
        }
        standardTile("sleepoff", "device.sleepoff", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
        	state "sleepoff", label: 'Sleep Off', action:"sleepoff", backgroundColor: "#ffffff", icon:"st.Weather.weather4"
            state "sleepoff", label: 'Sleep Off', action:"sleepoff", backgroundColor: "#ffffff", icon:"st.Weather.weather4"
        }
        standardTile("whoosh", "device.whoosh", width: 1, height: 1, canChangeIcon: false, canChangeBackground: true) {
        	state "whooshoff", label: 'Whoosh Off', action:"whooshon", backgroundColor: "#ffffff", icon:"st.Weather.weather1"
            state "whooshon", label: 'Whoosh On', action:"whooshoff", backgroundColor: "#ffc425", icon:"st.Weather.weather1"
        }
        
		main "sleep"
        details(["fan","fanlevel","light","lightlevel","sleepon","sleepoff","whoosh"])
	}
}


def parse(String description) {
	log.debug "Parsing '${description}'"
}


def fanon() {
	sendEvent(name: "fan", value: 'fanon')
	request("<" + devname+ ";FAN;PWR;ON>")
}

def fanoff() { 
	sendEvent(name: "fan", value: 'fanoff')
	request("<" + devname + ";FAN;PWR;OFF>")
}

def setfanlevel(val) {
	if (val==0) {
		sendEvent(name: "fan", value: "fanoff")
    } else {
    	sendEvent(name: "fan", value: "fanon")
    }
    sendEvent(name: "fanlevel", value: val)    
    request("<" + devname + ";FAN;SPD;SET;" + val + ">")
}

def lighton() {
	sendEvent(name: "light", value: 'lighton')
	request("<" + devname + ";LIGHT;PWR;ON>")
}

def lightoff() { 
	sendEvent(name: "light", value: 'lightoff')
	request("<" + devname + ";LIGHT;PWR;OFF>")
}

def sleepon() {
	sendEvent(name: "sleep", value: 'sleepon')
	request("<" + devname + ";SLEEP;STATE;ON>")
}

//def sleepoff() { 
//	sendEvent(name: "sleep", value: 'sleepoff')
//	request("<" + devname + ";SLEEP;STATE;OFF>")
//}

   

def sleepoff() { 
	sendEvent(name: "sleepoff", value: 'sleepoff')
	request("<" + devname + ";SLEEP;STATE;OFF>")
}

def whooshon() {
	sendEvent(name: "whoosh", value: 'whooshon')
	request("<" + devname + ";FAN;WHOOSH;ON>")
}

def whooshoff() { 
	sendEvent(name: "whoosh", value: 'whooshoff')
	request("<" + devname + ";FAN;WHOOSH;OFF>")
}

def setlightlevel(val) {
	if (val==0) {
    	sendEvent(name: "light", value: "lightoff")
    } else {    
		sendEvent(name: "light", value: "lighton")
    }
    sendEvent(name: "lightlevel", value: val)    
    request("<" + devname + ";LIGHT;LEVEL;SET;" + val + ">")
}

def request(body) { 

    def hosthex = convertIPtoHex(destIp)
    def porthex = convertPortToHex(31415)
    device.deviceNetworkId = "$hosthex:$porthex" 	
    def cmds = []
    def hubAction = new physicalgraph.device.HubAction(body,physicalgraph.device.Protocol.LAN)
	cmds << hubAction

    log.debug cmds
        
    cmds
}


private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}
