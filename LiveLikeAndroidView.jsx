import React, {useEffect, useRef, useState} from 'react';
import {findNodeHandle, NativeModules, requireNativeComponent, UIManager} from 'react-native';

export const LiveLikeChatWidgetView = requireNativeComponent('LiveLikeChatWidgetView');
export const LiveLikeWidgetView = requireNativeComponent('LiveLikeWidgetView');

const clientId = "OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI"


// ClientID = BJSFlQAxraN9F99EcVOzpva7G8ohtJdGKpRdx3Ml
// let programId = "5337f725-f580-49b5-9697-822f69e6d16e"
// let chatRoomId = "65735146-5f90-4b75-bbcc-e1b75eff6014"

// const programId = "08c5c27e-952d-4392-bd2a-c042db036ac5"
// const chatRoomId = "bda23d2a-da84-4fc1-bd39-7e9ddba73d71" // TODO: Pinned Message
// const chatRoomId = "bda23d2a-da84-4fc1-bd39-7e9ddba73d71" // TODO: Video Pinned New


// Messages + Pinned Video
const programId = "5337f725-f580-49b5-9697-822f69e6d16e"
const chatRoomId = "1ad3b3ae-c25f-4f3b-8873-727b1bf7ebbb"
// // https://cf-blast.livelikecdn.com/producer/applications/BJSFlQAxraN9F99EcVOzpva7G8ohtJdGKpRdx3Ml/chat-rooms/1ad3b3ae-c25f-4f3b-8873-727b1bf7ebbb/pinned-messages


// Sequential pinned messages
// const programId = "5337f725-f580-49b5-9697-822f69e6d16e"
// const chatRoomId = "65735146-5f90-4b75-bbcc-e1b75eff6014"
// const data = "{programId: '5337f725-f580-49b5-9697-822f69e6d16e', chatRoomId: '65735146-5f90-4b75-bbcc-e1b75eff6014', userAvatarUrl: 'https://websdk.livelikecdn.com/demo/assets/images/redrobot.png'}"
// https://cf-blast.livelikecdn.com/producer/applications/OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI/chat-rooms/65735146-5f90-4b75-bbcc-e1b75eff6014


const {LiveLikeModule} = NativeModules


const sendMessage = (viewId, message, timeOut) => {
    setTimeout(() => {
        console.log('DEBUG: MESSAGE SENT: ', message)
        UIManager.dispatchViewManagerCommand(
            viewId,
            UIManager.LiveLikeChatWidgetView.Commands.sendMessage.toString(),
            [viewId, message]
        );
    }, timeOut)
}


const updateNickName = (viewId, nickName) => {
    UIManager.dispatchViewManagerCommand(
        viewId,
        UIManager.LiveLikeChatWidgetView.Commands.updateNickName.toString(),
        [viewId, nickName]
    );
}

const updateUserAvatar = (viewId, userAvatar) => {
    UIManager.dispatchViewManagerCommand(
        viewId,
        UIManager.LiveLikeChatWidgetView.Commands.updateUserAvatar.toString(),
        [viewId, userAvatar]
    );
}

let redAvatar = "https://websdk.livelikecdn.com/demo/assets/images/redrobot.png"
let yellowAvatar = "https://websdk.livelikecdn.com/demo/assets/images/yellowrobot.png"


const TIME_DELAY = 13000

export const LiveLikeAndroidView = () => {

    const [show, setShow] = useState(true)
    const [showAskWidget, setShowAskWidget] = useState(false)
    const ref = useRef(null);

    useEffect(() => {
        LiveLikeModule.initializeSDK(clientId)
    }, [])


    useEffect(() => {
        console.log('SHOW CHAT: ', show)
    }, [show])

    useEffect(() => {
        console.log('SHOW ASK: ', showAskWidget)
    }, [showAskWidget])


    // useEffect(() => {
    //     let viewId = findNodeHandle(ref.current)
    //     setTimeout(() => {
    //         updateUserAvatar(viewId, yellowAvatar)
    //     }, 5000)
    // }, [])

    // useEffect(() => {
    //     setTimeout(() => {
    //         setShowAskWidget(true)
    //     }, 5000)
    // }, [])
    //
    //
    // useEffect(() => {
    //     setTimeout(() => {
    //         setShow(!show)
    //     }, TIME_DELAY)
    // }, [show])
    //
    // useEffect(() => {
    //     let viewId = findNodeHandle(ref.current)
    //     if (show && viewId) {
    //         let message1 = 'Send ' + Math.floor(Math.random() * 100)
    //         let message2 = 'Send ' + Math.floor(Math.random() * 100)
    //         sendMessage(viewId, message1, 5000)
    //         sendMessage(viewId, message2, 8000)
    //     }
    // }, [show])


    // useEffect(() => {
    //     setTimeout(() => {
    //         programId = "08c5c27e-952d-4392-bd2a-c042db036ac5"
    //         chatRoomId = "bda23d2a-da84-4fc1-bd39-7e9ddba73d71"
    //     }, TIME_DELAY * 1.5)
    // }, [])

    return (
        <LiveLikeChatWidgetView
            ref={ref}
            data={JSON.stringify({
                programId,
                chatRoomId
            })}
            userAvatarUrl={"https://websdk.livelikecdn.com/demo/assets/images/redrobot.png"}
            // influencerName={"Harbajan Singh"}
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
            onVideoPlayed={(event) => {
                console.log('DEBUG: ON Video message Clicked', event.nativeEvent)
            }}
            onAskInfluencer={(event) => {
                console.log('DEBUG: ON ASK INFLUENCER', event.nativeEvent)
            }}
            onRemoveAllPinMessages={(event) => {
                console.log('DEBUG: ON REMOVE ALL PIN MESSAGES', event.nativeEvent)
            }}
        />
    )
};


/*

            <LiveLikeChatWidgetView
                ref={ref}
                data={JSON.stringify({
                    programId,
                    chatRoomId
                })}
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
                onVideoPlayed={(event) => {
                    console.log('DEBUG: ON Video message Clicked', event.nativeEvent.videoUrl)
                }}
                onAskInfluencer={(event) => {
                    console.log('DEBUG: ON ASK INFLUENCER', event.nativeEvent)
                }}
            />

 */

/*


            <View
                style={{
                    height: 50,
                    width: 50,
                    left: 0,
                    bottom: 0,
                    backgroundColor: 'red'
                }}
                onPress={() => {
                    setShow(!show)
                }}
            />


        <LiveLikeChatWidgetView
            ref={ref}
            data={JSON.stringify({
                programId,
                chatRoomId
            })}
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
            onVideoPlayed={(event) => {
                console.log('DEBUG: ON Video message Clicked', event.nativeEvent.videoUrl)
            }}
            onAskInfluencer={(event) => {
                console.log('DEBUG: ON ASK INFLUENCER', event.nativeEvent)
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
                    influencerName={"Harbajan Singh"}
                    onWidgetShown={(event) => {
                        console.log('DEBUG1:', 'widget shown')
                    }}
                    onWidgetHidden={(event) => {
                        console.log('DEBUG2:', 'widget hidden')
                    }}
                />
     </View>


    useEffect(() => {
        setTimeout(() => {
            LiveLikeModule.subscribeUserStream("nickName").then(nickName => {
                console.log('NICKNAME', nickName)
            })
        }, 2000)
    }, [])


    useEffect(() => {
        const newMessage = 'Hey, New Message' + Math.floor(Math.random() * 100)
        const viewId = findNodeHandle(ref.current);
        setTimeout(() => {
            sendMessage(viewId, newMessage)
        }, 5000)
    }, [])


 */