# Cordova MPact BLE Plugin

MPact SDK ble plugin for javascript api that provide native interface to MPact SDK ble client. User can use this plugin to talk MPact SDK native interface for Android and iOS using javascript interface.

User require to obtain MPact SDK library for Adnroid and iOS in-order to determine ble device


## Using
Clone the plugin

    $ git clone https://github.com/lawlessnut/cordova-mpact-ble.git

Create a new Cordova Project

    $ cordova create <your_project> com.example.<your_project> <project_name>
    
Install the plugin

    $ cd <your_project>
    $ cordova plugin add https://github.com/lawlessnut/cordova-mpact-ble.git
    

Edit `www/js/index.js` and add the following code inside `onDeviceReady`

```js
    var success = function(message) {
        alert(message);
    }

    var failure = function() {
        alert("Error calling Hello Plugin");
    }

    mpactble.deviceready("World", success, failure);
```

Install iOS or Android platform

    cordova platform add ios
    cordova platform add android
    
Run the code

    cordova run 

## API
#Methods
*mpactble.connectServer
*mpactble.deviceready
*mpactble.services
*mpactble.startScan
*mpactble.stopScan


# cordova-mpact-ble
