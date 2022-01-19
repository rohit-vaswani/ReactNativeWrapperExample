import React, { useEffect, useRef } from 'react';
import { UIManager, findNodeHandle } from 'react-native';
import { PixelRatio} from "react-native";
import { LiveLikeAndroidViewManager } from './android-view-manager';
import {Dimensions} from "react-native-web"

const createFragment = (viewId) =>
  UIManager.dispatchViewManagerCommand(
    viewId,
    // we are calling the 'create' command
    UIManager.LiveLikeAndroidViewManager.Commands.create.toString(),
    [viewId]
  );

export const LiveLikeAndroidView = () => {
  const ref = useRef(null);

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