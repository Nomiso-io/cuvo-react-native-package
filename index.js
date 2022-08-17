import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform, View } from 'react-native';
import { Callback } from '../src/types';
import invariant from 'invariant';
import { perPlatformTypes } from './fileTypes';
import AudioRecorderPlayer from './audioRecoder';

export const types = perPlatformTypes[Platform.OS];
const CuvoDocumentPicker = NativeModules.CuvoDocumentPicker;

const audioRecorderPlayer = new AudioRecorderPlayer();
var audioPath = '';

export function takeCamera(options: String, callback: Callback) {
  NativeModules.CUVOTakeScreenShot.takePhoto('',
  callback);
}
export function startScreenRecording(options: String ) {
  if(Platform.OS ='ios') {
    NativeModules.CuvoRecordingPackage.startRecording('');
  }
  else {
    NativeModules.ScreenRecordingPackage.startRecording('');
  }
}
export function stopVideoRecording(callback: Callback) {
  if(Platform.OS ='ios') {
    NativeModules.CuvoRecordingPackage.stopScreenRecording('', callback);
  }
  else {
    NativeModules.ScreenRecordingPackage.stopRecording(callback);
  }
}
export function createAudioPlayer() {
    return new AudioRecorderPlayer();
}
export async function startAudioRecording () {
  var param = {
  }
  audioRecorderPlayer.startRecorder('DEFAULT', param, false).then(audioDetail => {
      audioPath = audioDetail
    })
    audioRecorderPlayer.addRecordBackListener((e) => {
      // console.log('audioRecorderPlayer --------',audioRecorderPlayer.mmssss(
      //   Math.floor(e.current_position),
      // ));
      audioDuration = e.current_position;
    return;
    });
  // NativeModules.CuvoAudioRecorderPlayer.startRecorder( 'DEFAULT', false, param).then(audioDetail => {
  //   audioPath = audioDetail
  // })
}
export async function stopAudioRecording () {
  audioRecorderPlayer.stopRecorder().then(audioDetail => {
  })
  var param = {
    audioPath: audioPath,
    audioDuration: audioDuration,
  }
  return param;
  // NativeModules.CuvoAudioRecorderPlayer.stopRecorder().then(audioDetail => {
  // })
  // return audioPath;
}
export async function startAudioPlay (audioPath: String) {
  let myRecord: Record<string, string> = {};
  myRecord["key"] = ""; 
  if(Platform.OS ='ios') {
    NativeModules.CuvoAudioRecorderPlayer.startPlayer('DEFAULT').then(audioDetail => {
    })
  }
  else {
    NativeModules.CuvoAudioRecorderPlayer.startPlayer('DEFAULT' , myRecord).then(audioDetail => {
    })
  }
  return '';
}
export async function stopAudioPlay () {
  NativeModules.CuvoAudioRecorderPlayer.stopPlayer().then(audioDetail => {
  })
}
export async function pickDirectory(params) {
  if (Platform.OS === 'ios') {
    const result = await pick({ ...params,
      mode: 'open',
      allowMultiSelection: false,
      type: ['public.folder']
    });
    return {
      uri: result[0].uri
    };
  } else {
    return CuvoDocumentPicker.pickDirectory();
  }
}

export function pickMultiple(opts) {
  const options = { ...opts,
    allowMultiSelection: true
  };
  return pick(options);
}
export function pickSingle(opts) {
  const options = { ...opts,
    allowMultiSelection: false
  };
  return pick(options).then(results => results[0]);
}
export function pick(opts) {
  const options = {
    // must be false to maintain old (v5) behavior
    allowMultiSelection: false,
    type: [types.allFiles],
    ...opts
  };
  const newOpts = {
    presentationStyle: 'formSheet',
    transitionStyle: 'coverVertical',
    ...options,
    type: Array.isArray(options.type) ? options.type : [options.type]
  };
  return doPick(newOpts);
}

function doPick(options) {
  var _options$mode, _options$copyTo;

  invariant(!('filetype' in options), 'A `filetype` option was passed to DocumentPicker.pick, the correct option is `type`');
  invariant(!('types' in options), 'A `types` option was passed to DocumentPicker.pick, the correct option is `type`');
  invariant(options.type.every(type => typeof type === 'string'), `Unexpected type option in ${options.type}, did you try using a DocumentPicker.types.* that does not exist?`);
  invariant(options.type.length > 0, '`type` option should not be an empty array, at least one type must be passed if the `type` option is not omitted');
  invariant( // @ts-ignore TS2345: Argument of type 'string' is not assignable to parameter of type 'PlatformTypes[OS][keyof PlatformTypes[OS]]'.
  !options.type.includes('folder'), 'RN document picker: "folder" option was removed, use "pickDirectory()"');

  if ('mode' in options && !['import', 'open'].includes((_options$mode = options.mode) !== null && _options$mode !== void 0 ? _options$mode : '')) {
    throw new TypeError('Invalid mode option: ' + options.mode);
  }

  if ('copyTo' in options && !['cachesDirectory', 'documentDirectory'].includes((_options$copyTo = options.copyTo) !== null && _options$copyTo !== void 0 ? _options$copyTo : '')) {
    throw new TypeError('Invalid copyTo option: ' + options.copyTo);
  }

  return CuvoDocumentPicker.pick(options);
}

export function releaseSecureAccess(uris) {
  if (Platform.OS !== 'ios') {
    return Promise.resolve();
  }

  invariant(Array.isArray(uris) && uris.every(uri => typeof uri === 'string'), `"uris" should be an array of strings, was ${uris}`);
  return CuvoDocumentPicker.releaseSecureAccess(uris);
}
const E_DOCUMENT_PICKER_CANCELED = 'DOCUMENT_PICKER_CANCELED';
const E_DOCUMENT_PICKER_IN_PROGRESS = 'ASYNC_OP_IN_PROGRESS';
export function isCancel(err) {
  return isErrorWithCode(err, E_DOCUMENT_PICKER_CANCELED);
}
export function isInProgress(err) {
  return isErrorWithCode(err, E_DOCUMENT_PICKER_IN_PROGRESS);
}

function isErrorWithCode(err, errorCode) {
  if (err instanceof Error && 'code' in err) {
    const nativeModuleErrorInstance = err;
    return (nativeModuleErrorInstance === null || nativeModuleErrorInstance === void 0 ? void 0 : nativeModuleErrorInstance.code) === errorCode;
  }

  return false;
}
export default {
  isCancel,
  releaseSecureAccess,
  pickDirectory,
  pick,
  pickMultiple,
  pickSingle,
  types,
  perPlatformTypes,
  AudioRecorderPlayer,
};


