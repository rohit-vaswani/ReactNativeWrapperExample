import React, {useEffect, useRef} from 'react';
import {NativeModules, requireNativeComponent, View} from 'react-native';

export const LiveLikeChatWidgetView = requireNativeComponent('LiveLikeChatWidgetView');
export const LiveLikeWidgetView = requireNativeComponent('LiveLikeWidgetView');


const programId = "08c5c27e-952d-4392-bd2a-c042db036ac5"
// const programId = "319a5414-dd78-49d2-b0cb-abdef76d29b7" // TODO: No published widget
const clientId = "OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI"
const chatRoomId = "32d1d38b-6321-4f45-ab38-05750792547d"
// const chatRoomId = "bda23d2a-da84-4fc1-bd39-7e9ddba73d71" // TODO: Pinned Message


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


    useEffect(() => {

        setTimeout(() => {
            setShow(true)
        }, 5000)

    }, [])

    useEffect(() => {


        setTimeout(() => {

            // LiveLikeModule.getChatRoomName(chatRoomId).then((roomName, error) => {
            //     console.log('Room name', roomName)
            // })
            //
            // LiveLikeModule.getCurrentUserProfileId().then((profileId, error) =>  {
            //     console.log('Profile ID', profileId)
            // })

        }, 2000)

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

    const [show, setShow] = React.useState(false)


    useEffect(() => {
        console.log('SHOW value', show)
    },[show])

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


    <LiveLikeChatWidgetView
        programId={programId}
        chatRoomId={chatRoomId}
        userAvatarUrl={"https://websdk.livelikecdn.com/demo/assets/images/redrobot.png"}
        userNickName={"Rohit Vasw New"}
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



    <LiveLikeWidgetView
        programId={programId}
        showAskWidget={show}
        style={{flex: 1}}
    />


    <View
        style={{
            backgroundColor: 'red',
            height: 50,
            width: 50,
            position: 'absolute',
            left: 0,
            bottom: 0
        }}
        onClick={() => {
            setShow(true)
            setTimeout(() => {
                setShow(false)
            }, 3000)
        }}
    />





 */