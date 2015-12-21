
module.exports = {
    connectServer : function(name,successCallBack,errorCallback,array){
        console.log('<==== CALLING '+name+' FUNCTION ====>');
        cordova.exec(successCallBack, errorCallback, "Hello", "connectServer",array);
    },
    deviceready : function(name,successCallBack,errorCallback){
        console.log('<==== CALLING '+name+' FUNCTION ====>');
        cordova.exec(successCallBack, errorCallback, "Hello", "deviceready");
    },
    services : function(deviceHandle,successCallBack,errorCallback){
        console.log('<==== CALLING '+deviceHandle+' FUNCTION ====>');
        cordova.exec(successCallBack, errorCallback, "Hello", "services", [deviceHandle]);
    },
    startScan : function(deviceHandle,successCallBack,errorCallback){
        console.log('<==== CALLING '+deviceHandle+' FUNCTION ====>');
        cordova.exec(successCallBack, errorCallback, "Hello", "startScan", [deviceHandle]);
    }
};
