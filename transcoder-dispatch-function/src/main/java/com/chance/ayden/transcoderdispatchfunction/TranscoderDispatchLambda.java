package com.chance.ayden.transcoderdispatchfunction;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertAsyncClient;
import software.amazon.awssdk.services.mediaconvert.model.AacCodingMode;
import software.amazon.awssdk.services.mediaconvert.model.AacSettings;
import software.amazon.awssdk.services.mediaconvert.model.AntiAlias;
import software.amazon.awssdk.services.mediaconvert.model.AudioCodec;
import software.amazon.awssdk.services.mediaconvert.model.AudioCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.AudioDefaultSelection;
import software.amazon.awssdk.services.mediaconvert.model.AudioDescription;
import software.amazon.awssdk.services.mediaconvert.model.AudioSelector;
import software.amazon.awssdk.services.mediaconvert.model.ColorMetadata;
import software.amazon.awssdk.services.mediaconvert.model.ContainerSettings;
import software.amazon.awssdk.services.mediaconvert.model.ContainerType;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.DestinationSettings;
import software.amazon.awssdk.services.mediaconvert.model.DropFrameTimecode;
import software.amazon.awssdk.services.mediaconvert.model.FileGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.FrameCaptureSettings;
import software.amazon.awssdk.services.mediaconvert.model.H264FramerateControl;
import software.amazon.awssdk.services.mediaconvert.model.H264FramerateConversionAlgorithm;
import software.amazon.awssdk.services.mediaconvert.model.H264InterlaceMode;
import software.amazon.awssdk.services.mediaconvert.model.H264QualityTuningLevel;
import software.amazon.awssdk.services.mediaconvert.model.H264QvbrSettings;
import software.amazon.awssdk.services.mediaconvert.model.H264RateControlMode;
import software.amazon.awssdk.services.mediaconvert.model.H264SceneChangeDetect;
import software.amazon.awssdk.services.mediaconvert.model.H264Settings;
import software.amazon.awssdk.services.mediaconvert.model.Input;
import software.amazon.awssdk.services.mediaconvert.model.InputTimecodeSource;
import software.amazon.awssdk.services.mediaconvert.model.JobSettings;
import software.amazon.awssdk.services.mediaconvert.model.Mp4Settings;
import software.amazon.awssdk.services.mediaconvert.model.Output;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroup;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupType;
import software.amazon.awssdk.services.mediaconvert.model.S3DestinationSettings;
import software.amazon.awssdk.services.mediaconvert.model.S3StorageClass;
import software.amazon.awssdk.services.mediaconvert.model.ScalingBehavior;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodec;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.VideoDescription;
import software.amazon.awssdk.services.mediaconvert.model.VideoSelector;
import software.amazon.awssdk.services.mediaconvert.model.VideoTimecodeInsertion;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranscoderDispatchLambda implements RequestHandler<S3Event, Void> {

  private final String s3OutputUri;

  private final String thumbsOutput;

  private final String mp4Output;

  private final String mediaConvertRoleArn;

  private static final MediaConvertAsyncClient CLIENT;

  public TranscoderDispatchLambda(
	  @ConfigProperty(name = "media-convert.role-arn") String mediaConvertRoleArn,
	  @ConfigProperty(name = "s3.transcoded-bucket-name") String transcodedBucketName
  ) {
	this.mediaConvertRoleArn = mediaConvertRoleArn;
	this.s3OutputUri = "s3://%s/".formatted(transcodedBucketName);
	this.thumbsOutput = this.s3OutputUri + "thumbs/";
	this.mp4Output = this.s3OutputUri + "mp4/";
  }

  // Client priming for AWS Lambda.
  static {
	CLIENT = MediaConvertAsyncClient.builder()
		.region(Region.US_EAST_1) // Improves performance
		.httpClient(AwsCrtAsyncHttpClient.create())
		.build();
  }

  private final Output.Builder baseOutputBuilder = Output.builder()
	  .audioDescriptions(List.of(AudioDescription.builder()
		  .codecSettings(AudioCodecSettings.builder()
			  .codec(AudioCodec.AAC)
			  .aacSettings(AacSettings.builder()
				  .bitrate(96000)
				  .codingMode(AacCodingMode.CODING_MODE_2_0)
				  .sampleRate(48000)
				  .build())
			  .build())
		  .build()))
	  .containerSettings(ContainerSettings.builder()
		  .container(ContainerType.MP4)
		  .mp4Settings(Mp4Settings.builder()
			  .build())
		  .build());
  private final Output ultraLowResOutput = baseOutputBuilder
	  .nameModifier("-ultra-low-res")
	  .videoDescription(VideoDescription.builder()
		  .height(360)
		  .codecSettings(VideoCodecSettings.builder()
			  .codec(VideoCodec.H_264)
			  .h264Settings(H264Settings.builder()
				  .interlaceMode(H264InterlaceMode.PROGRESSIVE)
				  .framerateDenominator(1)
				  .maxBitrate(1000000)
				  .framerateControl(H264FramerateControl.SPECIFIED)
				  .rateControlMode(H264RateControlMode.QVBR)
				  .qvbrSettings(H264QvbrSettings.builder()
					  .qvbrQualityLevel(6)
					  .build())
				  .framerateNumerator(30)
				  .sceneChangeDetect(H264SceneChangeDetect.TRANSITION_DETECTION)
				  .qualityTuningLevel(H264QualityTuningLevel.SINGLE_PASS)
				  .framerateConversionAlgorithm(H264FramerateConversionAlgorithm.DUPLICATE_DROP)
				  .build())
			  .build())
		  .build())
	  .build();
  private final Output lowResOutput = baseOutputBuilder
	  .nameModifier("-low-res")
	  .videoDescription(VideoDescription.builder()
		  .height(480)
		  .codecSettings(VideoCodecSettings.builder()
			  .codec(VideoCodec.H_264)
			  .h264Settings(H264Settings.builder()
				  .interlaceMode(H264InterlaceMode.PROGRESSIVE)
				  .framerateDenominator(1)
				  .maxBitrate(2500000)
				  .framerateControl(H264FramerateControl.SPECIFIED)
				  .rateControlMode(H264RateControlMode.QVBR)
				  .qvbrSettings(H264QvbrSettings.builder()
					  .qvbrQualityLevel(6)
					  .qvbrQualityLevelFineTune(0.33)
					  .build())
				  .framerateNumerator(30)
				  .sceneChangeDetect(H264SceneChangeDetect.TRANSITION_DETECTION)
				  .qualityTuningLevel(H264QualityTuningLevel.SINGLE_PASS)
				  .framerateConversionAlgorithm(H264FramerateConversionAlgorithm.DUPLICATE_DROP)
				  .build())
			  .build())
		  .build())
	  .build();
  private final Output mediumResOutput = baseOutputBuilder
	  .nameModifier("-medium-res")
	  .videoDescription(VideoDescription.builder()
		  .height(720)
		  .codecSettings(VideoCodecSettings.builder()
			  .codec(VideoCodec.H_264)
			  .h264Settings(H264Settings.builder()
				  .interlaceMode(H264InterlaceMode.PROGRESSIVE)
				  .framerateDenominator(1)
				  .maxBitrate(5000000)
				  .framerateControl(H264FramerateControl.SPECIFIED)
				  .rateControlMode(H264RateControlMode.QVBR)
				  .qvbrSettings(H264QvbrSettings.builder()
					  .qvbrQualityLevel(6)
					  .qvbrQualityLevelFineTune(0.66)
					  .build())
				  .framerateNumerator(30)
				  .sceneChangeDetect(H264SceneChangeDetect.TRANSITION_DETECTION)
				  .qualityTuningLevel(H264QualityTuningLevel.SINGLE_PASS)
				  .framerateConversionAlgorithm(H264FramerateConversionAlgorithm.DUPLICATE_DROP)
				  .build())
			  .build())
		  .build())
	  .build();
  private final Output highResOutput = baseOutputBuilder
	  .nameModifier("-high-res")
	  .videoDescription(VideoDescription.builder()
		  .height(1080)
		  .codecSettings(VideoCodecSettings.builder()
			  .codec(VideoCodec.H_264)
			  .h264Settings(H264Settings.builder()
				  .interlaceMode(H264InterlaceMode.PROGRESSIVE)
				  .framerateDenominator(1)
				  .maxBitrate(8000000)
				  .framerateControl(H264FramerateControl.SPECIFIED)
				  .rateControlMode(H264RateControlMode.QVBR)
				  .qvbrSettings(H264QvbrSettings.builder()
					  .qvbrQualityLevel(7)
					  .build())
				  .framerateNumerator(30)
				  .sceneChangeDetect(H264SceneChangeDetect.TRANSITION_DETECTION)
				  .qualityTuningLevel(H264QualityTuningLevel.SINGLE_PASS)
				  .framerateConversionAlgorithm(H264FramerateConversionAlgorithm.DUPLICATE_DROP)
				  .build())
			  .build())
		  .build())
	  .build();

  @Override
  public Void handleRequest(S3Event input, Context context) {
	createMediaJob(input);
	return null;
  }

  private void createMediaJob(S3Event s3Event) {

	String s3ObjectARN = parseS3EventARN(s3Event);
	CreateJobResponse createJobResponse;

	Log.debug("MediaConvert role ARN: " + mediaConvertRoleArn);
	Log.debug("MediaConvert input file ARN: " + s3ObjectARN);
	Log.debug("MediaConvert output base path: " + s3OutputUri);

	OutputGroup fileMp4 = createMp4OutputGroup();
	OutputGroup thumbsGroup = createThumbsOutputGroup();

	JobSettings jobSettings = createJobSettings(s3ObjectARN, fileMp4, thumbsGroup);

	CreateJobRequest mediaConvertJob = CreateJobRequest.builder()
		.role(mediaConvertRoleArn)
		.settings(jobSettings)
		.build();

	CLIENT.createJob(mediaConvertJob);
	Log.info("Created MediaConvert job");

  }

  private String parseS3EventARN(S3Event s3Event) {
	Log.info("Received request body: " + s3Event);
	S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().getFirst();

	// Extract bucket name and object key
	String bucketName = URLDecoder.decode(record.getS3().getBucket().getName(), StandardCharsets.UTF_8);
	String objectKey = URLDecoder.decode(record.getS3().getObject().getKey(), StandardCharsets.UTF_8);

	Log.debug("Extracted bucket name: " + bucketName);
	Log.debug("Extracted object key: " + objectKey);

	// Construct the ARN
	String s3ObjectARN = String.format("s3://%s/%s", bucketName, objectKey);

	Log.info("Attempting to create media job with ARN: " + s3ObjectARN);
	return s3ObjectARN;
  }

  private JobSettings createJobSettings(String s3InputFileURI, OutputGroup... outputGroups) {
	Map<String, AudioSelector> audioSelectors = new HashMap<>();
	audioSelectors.put("Audio Selector 1", AudioSelector.builder().defaultSelection(AudioDefaultSelection.DEFAULT).offset(0).build());

	return JobSettings.builder()
		.followSource(1)
		.inputs(
			Input.builder()
				.audioSelectors(audioSelectors)
				.videoSelector(VideoSelector.builder().build())
				.timecodeSource(InputTimecodeSource.ZEROBASED)
				.fileInput(s3InputFileURI)
				.build())
		.outputGroups(outputGroups)
		.build();
  }

  private OutputGroup createMp4OutputGroup() {
	return OutputGroup.builder()
		.name("File Group")
		.outputGroupSettings(
			OutputGroupSettings.builder()
				.type(OutputGroupType.FILE_GROUP_SETTINGS)
				.fileGroupSettings(
					FileGroupSettings.builder()
						.destination(mp4Output)
						.destinationSettings(
							DestinationSettings.builder()
								.s3Settings(
									S3DestinationSettings.builder()
										.storageClass(S3StorageClass.STANDARD)
										.build())
								.build())
						.build())
				.build())
		.outputs(
			ultraLowResOutput,
			lowResOutput,
			mediumResOutput,
			highResOutput
		).build();
  }

  private OutputGroup createThumbsOutputGroup() {
	return OutputGroup.builder()
		.name("File Group")
		.customName("thumbs")
		.outputGroupSettings(OutputGroupSettings.builder()
			.type(OutputGroupType.FILE_GROUP_SETTINGS)
			.fileGroupSettings(FileGroupSettings.builder()
				.destination(thumbsOutput).build())
			.build())
		.outputs(Output.builder().extension("jpg")
			.containerSettings(ContainerSettings.builder()
				.container(ContainerType.RAW).build())
			.videoDescription(VideoDescription.builder()
				.height(480)
				.scalingBehavior(ScalingBehavior.DEFAULT)
				.sharpness(50).antiAlias(AntiAlias.ENABLED)
				.timecodeInsertion(
					VideoTimecodeInsertion.DISABLED)
				.colorMetadata(ColorMetadata.INSERT)
				.dropFrameTimecode(DropFrameTimecode.ENABLED)
				.codecSettings(VideoCodecSettings.builder()
					.codec(VideoCodec.FRAME_CAPTURE)
					.frameCaptureSettings(
						FrameCaptureSettings
							.builder()
							.framerateNumerator(1)
							.framerateDenominator(1)
							.maxCaptures(1)
							.quality(80)
							.build())
					.build())
				.build())
			.build())
		.build();
  }
}
