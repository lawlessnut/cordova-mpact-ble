#import <Cordova/CDV.h>
#import <MPactClient/MPactClient.h>

@interface HWPHello : CDVPlugin <MPactClientDelegate>
{
    MPactClient *mpactClient;
}

- (void) greet:(CDVInvokedUrlCommand*)command;
-(void) deviceready : (CDVInvokedUrlCommand*) command;
-(void) connectServer : (CDVInvokedUrlCommand*) commnad;
-(void) startScan : (CDVInvokedUrlCommand*) command;
-(void) stopScan  : (CDVInvokedUrlCommand*) commnad;
-(void) services : (CDVInvokedUrlCommand*) command;



@end