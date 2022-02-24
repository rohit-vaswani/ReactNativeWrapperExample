import React, {useEffect, useRef, useState} from 'react';
import {findNodeHandle, NativeModules, requireNativeComponent, UIManager, View} from 'react-native';

export const LiveLikeChatWidgetView = requireNativeComponent('LiveLikeChatWidgetView');
export const LiveLikeWidgetView = requireNativeComponent('LiveLikeWidgetView');

const clientId = "OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI"




// const programId = "08c5c27e-952d-4392-bd2a-c042db036ac5"
// const chatRoomId = "bda23d2a-da84-4fc1-bd39-7e9ddba73d71" // TODO: Pinned Message
// const chatRoomId = "bda23d2a-da84-4fc1-bd39-7e9ddba73d71" // TODO: Video Pinned New


// New Pin Message portal
// const programId =  "5337f725-f580-49b5-9697-822f69e6d16e"
// const chatRoomId = "1ad3b3ae-c25f-4f3b-8873-727b1bf7ebbb"
// // https://cf-blast.livelikecdn.com/producer/applications/BJSFlQAxraN9F99EcVOzpva7G8ohtJdGKpRdx3Ml/chat-rooms/1ad3b3ae-c25f-4f3b-8873-727b1bf7ebbb/pinned-messages


// const programId =  "5337f725-f580-49b5-9697-822f69e6d16e"
// const chatRoomId = "65735146-5f90-4b75-bbcc-e1b75eff6014"
// https://cf-blast.livelikecdn.com/producer/applications/OPba08mrr8gLZ2UMQ3uWMBOLiGhfovgIeQAEfqgI/chat-rooms/65735146-5f90-4b75-bbcc-e1b75eff6014


const programId =  "5337f725-f580-49b5-9697-822f69e6d16e"
const chatRoomId = "1ad3b3ae-c25f-4f3b-8873-727b1bf7ebbb"


const {LiveLikeModule} = NativeModules


const sendMessage = (viewId, message) => {
    UIManager.dispatchViewManagerCommand(
        viewId,
        UIManager.LiveLikeChatWidgetView.Commands.sendMessage.toString(),
        [viewId, message]
    );
}


const updateNickName = (viewId, nickName) => {
    UIManager.dispatchViewManagerCommand(
        viewId,
        UIManager.LiveLikeChatWidgetView.Commands.updateNickName.toString(),
        [viewId, nickName]
    );
}


export const LiveLikeAndroidView = () => {

    const [show, setShow] = useState(false)

    useEffect(() => {
        LiveLikeModule.initializeSDK(clientId)
    }, [])


    useEffect(() => {

        setTimeout(() => {
            setShow(true)
        }, 2000)

    }, [])

    useEffect(() => {
        console.log('Updated', show)
    }, [show])


    const ref = useRef(null);

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
            onVideoPlayed={(event) => {
                console.log('DEBUG: ON Video message Clicked', event.nativeEvent.videoUrl)
            }}
            onAskInfluencer={(event) => {
                console.log('DEBUG: ON ASK INFLUENCER', event.nativeEvent)
            }}
        />
    )

};


/*


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