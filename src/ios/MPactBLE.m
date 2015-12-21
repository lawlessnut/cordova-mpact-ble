#import "HWPHello.h"

@implementation HWPHello{
    NSString *server;
    NSString *port;
    NSString *username;
    NSString *password;
    NSString *uuid ;
    NSString *closestTag;
}

- (void)greet:(CDVInvokedUrlCommand*)command
{

    NSString* callbackId = [command callbackId];
    NSString* name = [[command arguments] objectAtIndex:0];
    NSString* msg = [NSString stringWithFormat: @"Hello, %@", name];

    CDVPluginResult* result = [CDVPluginResult
                               resultWithStatus:CDVCommandStatus_OK
                               messageAsString:msg];

    [self success:result callbackId:callbackId];
}
-(void) startScan:(CDVInvokedUrlCommand *)command{
    NSString *callback = [command callbackId];
    [mpactClient Start];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsBool:true];
    [self success:result callbackId:callback];
}

-(void) stopScan:(CDVInvokedUrlCommand *)commnad{
    
}

-(void)services:(CDVInvokedUrlCommand*)command{
    NSString *callbackId = [command callbackId];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsString:closestTag];
    [self success:result callbackId:callbackId];
}

-(void) connectServer:(CDVInvokedUrlCommand *)commnad{
   
        uuid = @"FE913213-B311-4A42-8C16-47FAEAC938DB";
    //    NSString* callbackId = [commnad callbackId];
    mpactClient = [MPactClient initClient];
    
    NSDictionary* dict = [[commnad arguments]objectAtIndex:0];
    for(id key in dict){
        NSLog(@"key =%@ value=%@",key,[dict objectForKey:key]);
        if([key  isEqual: @"ip"]){
            server = [dict objectForKey:key];
        }else if([key isEqual: @"port"]){
            port = [dict objectForKey:key];
        }else if([key isEqual:@"username"]){
            username = [dict objectForKey:key];
        }else if([key isEqual:@"password"]){
            password = [dict objectForKey:key];
        }
    }
    
    mpactClient.server = [[MPactServerInfo alloc]initWithNetwork:server AndPort:[port intValue]];
    
    mpactClient.server.loginID = username;
    mpactClient.server.password = password;
    mpactClient.server.authenticate= false;
    mpactClient.iBeaconUUID = [[NSUUID alloc]initWithUUIDString:uuid];
    mpactClient.beaconType = MPactBeaconTypeMPact;
    mpactClient.proximityRange = CLProximityNear;
    
    NSLog(@"CLINET ID %@",mpactClient.clientId);
    [mpactClient setDelegate:self];
//    [mpactClient Start];

}

-(void) deviceready:(CDVInvokedUrlCommand *)command{
    NSLog(@"Device ready event fire");
}

-(void) MPactClient:(id)client ClosestTag:(MPactTag *)tag{
    NSLog(@"closest tag %@", [tag tagID]);
    closestTag = [tag tagID];
//    closestTag = [NSString stringWithFormat:@"%i",[tag rssi]];
}

-(void) MPactClient:(id)client didDetermineState:(CLRegionState)state{
    NSLog(@"did determine state");
}

-(void) MPactClient:(id)client didDetermineState:(CLRegionState)state Major:(NSNumber *)major Minor:(NSNumber *)minor{
    NSLog(@"Did determineState for MAJOR MINOR");
}

-(void) MPactClient:(id)client didDetermineState:(CLRegionState)state CategoryValue:(MPactCategoryValue *)categoryValue{
    NSLog(@"Did determineState for CATEGORY");
}

-(void) MPactClient:(id)client didChangeBeaconType:(MPactBeaconType)beaconType{
    NSLog(@"Did change beacon type");
}

-(void) MPactClient:(id)client didChangeProximityRange:(CLProximity)proximityRange{
    NSLog(@"Did change proximity");
}

-(void) MPactClient:(id)client didChangeIBeaconUUID:(NSUUID *)iBeaconUUID{
    NSLog(@"Did change IBEACON UUID");
}

@end