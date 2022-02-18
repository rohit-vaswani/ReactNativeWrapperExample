import React, {useEffect, useRef} from 'react';
import {findNodeHandle, NativeModules, requireNativeComponent, UIManager} from 'react-native';

export const LiveLikeChatWidgetView = requireNativeComponent('LiveLikeChatWidgetView');
export const LiveLikeWidgetView = requireNativeComponent('LiveLikeWidgetView');


const programId = "08c5c27e-952d-4392-bd2a-c042db036ac5"
// const programId = "319a5414-dd78-49d2-b0cb-abdef76d29b7" // TODO: No published widget
const clientId = "OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI"
// const chatRoomId = "32d1d38b-6321-4f45-ab38-05750792547d"
// const chatRoomId = "bda23d2a-da84-4fc1-bd39-7e9ddba73d71" // TODO: Pinned Message
// const chatRoomId = "bda23d2a-da84-4fc1-bd39-7e9ddba73d71" // TODO: Video Pinned New
const chatRoomId = "93b222a6-74dd-4f9e-a728-ca3addcdce8b" // TODO: Video Pinned New NEW


const {LiveLikeModule} = NativeModules


const sendMessage = (viewId, message) => {
    UIManager.dispatchViewManagerCommand(
        viewId,
        UIManager.LiveLikeChatWidgetView.Commands.sendMessage.toString(),
        [viewId, message]
    );
}


const updateNickName = (viewId, nickName) => {


    console.log("DEBUG: CHECK THIS: ", !!UIManager.LiveLikeChatWidgetView.Commands.updateNickName)

    UIManager.dispatchViewManagerCommand(
        viewId,
        UIManager.LiveLikeChatWidgetView.Commands.updateNickName.toString(),
        [viewId, nickName]
    );
}


export const LiveLikeAndroidView = () => {

    useEffect(() => {
        LiveLikeModule.initializeSDK(clientId)
    }, [])

    useEffect(() => {
        setTimeout(() => {
            LiveLikeModule.subscribeUserStream("nickName").then(nickName => {
                console.log('NICKNAME', nickName)
            })
        }, 2000)
    }, [])

    useEffect(() => {
        const viewId = findNodeHandle(ref.current);
        setTimeout(() => {
            updateNickName(viewId, "NEW NAME 2")
        }, 12000)

    }, [])


    // useEffect(() => {
    //     setTimeout(() => {
    //         setShow(true)
    //     }, 5000)
    // }, [])


    const ref = useRef(null);

    useEffect(() => {
        const newMessage = 'Hey, New Message' + Math.floor(Math.random() * 100)
        const viewId = findNodeHandle(ref.current);
        setTimeout(() => {
            sendMessage(viewId, newMessage)
        }, 5000)
    }, [])


    useEffect(() => {
        const newMessage = 'Hey, New Message' + Math.floor(Math.random() * 100)
        const viewId = findNodeHandle(ref.current);
        setTimeout(() => {
            sendMessage(viewId, newMessage)
        }, 7000)
    }, [])


    useEffect(() => {
        const newMessage = 'Hey, New Message' + Math.floor(Math.random() * 100)
        const viewId = findNodeHandle(ref.current);
        setTimeout(() => {
            sendMessage(viewId, newMessage)
        }, 10000)
    }, [])
    //
    // useEffect(() => {
    //     const viewId = findNodeHandle(ref.current);
    //     createFragment(viewId);
    // }, []);

    const [show, setShow] = React.useState(false)


    useEffect(() => {
        console.log('SHOW value', show)
    }, [show])

    return (
        <LiveLikeChatWidgetView
            ref={ref}
            programId={programId}
            chatRoomId={chatRoomId}
            userAvatarUrl={"https://websdk.livelikecdn.com/demo/assets/images/redrobot.png"}
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
            onChatMessageSent={(event) => {
                console.log('DEBUG: ON CHAT MESSAGE SUCCESS', event.nativeEvent.message)
            }}
        />
    )

};


/*


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



<View style={{
                marginTop: 12,
                height: 500,
                width: '100%',
                position: 'absolute',
                left: 0,
                top: 0
            }}>
                <LiveLikeWidgetView
                    programId={programId}
                    showAskWidget={show}
                    style={{flex: 1}}
                    onWidgetShown={(event) => {
                        console.log('DEBUG1:', 'widget shown')
                    }}
                    onWidgetHidden={(event) => {
                        console.log('DEBUG2:', 'widget hidden')
                    }}
                />
            </View>


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