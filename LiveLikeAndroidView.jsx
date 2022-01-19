import React, {useEffect, useRef} from 'react';
import {findNodeHandle, PixelRatio, UIManager} from 'react-native';
import {LiveLikeAndroidViewManager} from './android-view-manager';


const createFragment = (viewId) => {
    UIManager.dispatchViewManagerCommand(
        viewId,
        // we are calling the 'create' command
        UIManager.LiveLikeAndroidViewManager.Commands.create.toString(),
        [viewId]
    );
}

// const sendMessage = (viewId, message) => {
//     UIManager.dispatchViewManagerCommand(
//         viewId,
//         // we are calling the 'sendMessage' command
//         UIManager.LiveLikeAndroidViewManager.Commands.sendMessage.toString(),
//         [viewId, message]
//     );
// }

export const LiveLikeAndroidView = () => {
    const ref = useRef(null);

    // TODO: Send Message
    // const newMessage = 'Hey, New Message'
    // useEffect(() => {
    //     const viewId = findNodeHandle(ref.current);
    //     setTimeout(() => {
    //         sendMessage(viewId, newMessage)
    //     }, 3000)
    // }, [])

    useEffect(() => {
        const viewId = findNodeHandle(ref.current);
        createFragment(viewId);
    }, []);

    return (
        <LiveLikeAndroidViewManager
            style={{
                // converts dpi to px, provide desired height
                height: PixelRatio.getPixelSizeForLayoutSize(750),
                // converts dpi to px, provide desired width
                width: PixelRatio.getPixelSizeForLayoutSize(420)
            }}
            ref={ref}
        />
    );
};