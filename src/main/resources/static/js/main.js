//self executing function here
(function() {
   //console.log("ready!");
   getData();
   
})();

function getData(){
	$.ajax({
    	type : "GET",
		url : "/map/getMapData",
		success : function(result) {
			let jsonArr = JSON.parse(result);
			//console.log(jsonArr);
			loadMap(jsonArr);
		},
		error : function(e) {
			console.err("getData err: ", e);
		}
    });
}

function loadMap(jsonArr){
	var mymap = L.map('mapid').setView([50.505, 30.09], 5);

	L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
		maxZoom: 18,
		attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
			'<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
			'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
		id: 'mapbox/streets-v11',
		tileSize: 512,
		zoomOffset: -1
	}).addTo(mymap);
	
	let addrs = jsonArr[0];
	let conections = jsonArr[1];
	
	let visitorsByAddr = new Map();
	let poitersMap = new Map();
	for (let i = 0; i < addrs.length; i++) {
		if(addrs[i].ip !== undefined){
			visitorsByAddr.set(addrs[i].ip, []);				
		}
		
		let point = addrs[i];
        let latitude = point.latitude;
        let longitude = point.longitude;
        if(!poitersMap.has(latitude+""+longitude)){
        	poitersMap.set(latitude+""+longitude, []);
        }
        poitersMap.get(latitude+""+longitude).push(point.ip);
	}
	//console.log(poitersMap);
	let realPeople = 0;
	for (let i = 0; i < conections.length; i++) {
		if(conections[i].name !== "bot"){
			realPeople++;
		}
		if(visitorsByAddr.has(conections[i].remoteAddr)){
			let value = visitorsByAddr.get(conections[i].remoteAddr);
			//console.log(conections[i].name);
			if(conections[i].name !== undefined){
				value.push(conections[i].name);
			}
			visitorsByAddr.set(conections[i].remoteAddr, value);
		}
	}
	//console.log(visitorsByAddr);
	
	let maxVist = 0;
	let maxAddr = 0;
	
	for (let [key, value] of visitorsByAddr) {
		if(value.length > maxVist){
			maxVist = value.length;
			maxAddr = key;
		}
	}
	let allVisits = visitorsByAddr.size;
	//console.log(maxAddr + ' = ' + maxVist);
	//console.log(' allVisits ' + allVisits);
	for (var i = 0; i < addrs.length; i++) {
		if(addrs[i].ip == maxAddr){
			document.getElementById("maxFreq").innerHTML = 
				"All visits : " + conections.length + "<br/>"
				+ "People : "+realPeople+"<br/>"
				+ "Bots : "+(allVisits - realPeople)+"<br/>"
				+ "Unique visits : "+allVisits+"<br/>"
				+ "Max visits : "+maxVist+", from  "+addrs[i].country_name+", "
				+addrs[i].state_prov+", "
				+addrs[i].district+", "
				+addrs[i].city+" (isp : "+addrs[i].isp+")";
			//console.log(addrs[i]);
		}
	}
	
    for(let i = 0; i < addrs.length; i++){
        let point = addrs[i];
        let latitude = point.latitude;
        let longitude = point.longitude;
        
        let names = "";
        if(poitersMap.has(latitude+""+longitude) && poitersMap.get(latitude+""+longitude).length > 1){
        	let ips = poitersMap.get(latitude+""+longitude);
        	for (let i = 0; i < ips.length; i++) {
        		let thisName = visitorsByAddr.get(ips[i]);
        		//console.log("if="+thisName);
        		names += visitorsByAddr.get(ips[i])+",";
        		//console.log("names="+names);
			}
        } else {
        	names += visitorsByAddr.get(point.ip);
        	//console.log("else="+names);
        }
        
        let allNames = names.split(",");
        let freqByName = new Map();
        for (let i = 0; i < allNames.length; i++) {
        	if (allNames[i] !== "") {
        		let tname = allNames[i];
        		if(!freqByName.has(tname)){
        			freqByName.set(tname, 1);
        		} else {
        			let freq = freqByName.get(tname);
        			freq++;
        			freqByName.set(tname, freq);
        		}
        	}
		}
        //console.log(freqByName);
        names = "";
        for (let [key, value] of freqByName) {
    	  //console.log(key + ' = ' + value);
    	  names += key +"("+value+"), "
    	}
        L.marker([latitude, longitude]).addTo(mymap).bindPopup(names);
        //console.log(i +" name="+names+"; latitude="+latitude+"; longitude="+longitude);
    }
    
    var popup = L.popup();
    mymap.on('click', onMapClick);
    
    function onMapClick(e) {
    	popup
    		.setLatLng(e.latlng)
    		.setContent("You clicked the map at " + e.latlng.toString())
    		.openOn(mymap);
    }
    
}



