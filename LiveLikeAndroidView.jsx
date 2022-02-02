import React, {useEffect, useRef} from 'react';
import {NativeModules, requireNativeComponent, View} from 'react-native';

export const LiveLikeChatWidgetView = requireNativeComponent('LiveLikeChatWidgetView');


const programId = "08c5c27e-952d-4392-bd2a-c042db036ac5"
const clientId = "OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI"
const chatRoomId = "32d1d38b-6321-4f45-ab38-05750792547d"


const {LiveLikeModule} = NativeModules


// const createFragment = (viewId) => {
//     UIManager.dispatchViewManagerCommand(
//         viewId,
//         // we are calling the 'create' command
//         UIManager.LiveLikeWidgetView.Commands.create.toString(),
//         [viewId]
//     );
// }

// const sendMessage = (viewId, message) => {
//     UIManager.dispatchViewManagerCommand(
//         viewId,
//         // we are calling the 'sendMessage' command
//         UIManager.LiveLikeWidgetView.Commands.sendMessage.toString(),
//         [viewId, message]
//     );
// }

export const LiveLikeAndroidView = () => {


    useEffect(() => {
        LiveLikeModule.initializeSDK(clientId)
    }, [])


    const ref = useRef(null);

    // TODO: Send Message
    // const newMessage = 'Hey, New Message'
    // useEffect(() => {
    //     const viewId = findNodeHandle(ref.current);
    //     setTimeout(() => {
    //         sendMessage(viewId, newMessage)
    //     }, 3000)
    // }, [])
    //
    // useEffect(() => {
    //     const viewId = findNodeHandle(ref.current);
    //     createFragment(viewId);
    // }, []);

    return (
        <>
            <LiveLikeChatWidgetView
                programId={programId}
                chatRoomId={chatRoomId}
                userAvatarUrl={"https://websdk.livelikecdn.com/demo/assets/images/redrobot.png"}
                userNickName={"Rohit Vaswani"}
                style={{flex: 1}}
                onWidgetShown={(event) => {
                    LayoutAnimation.configureNext(LayoutAnimation.Presets.linear)
                    this.setState({widgetHeight: event.nativeEvent.height})
                }}
                onWidgetHidden={(event) => {
                    LayoutAnimation.configureNext(LayoutAnimation.Presets.linear)
                    this.setState({widgetHeight: 0})
                }}
                onEvent={event => {
                }}
            />
        </>
    )
};


/*

            <View style={{
                position: 'absolute',
                left: 10,
                bottom: 10,
                backgroundColor: 'red',
                height: 40,
                width: 40
            }}/>

 */



/*



 */