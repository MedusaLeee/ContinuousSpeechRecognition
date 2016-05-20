package com.viathink.vad;
/**
 * 此类是通过tarsos提供的进程的方式去检测静音，这个文件只是备份，在本demo中未使用，同步识别方法见STT.java
 */
import java.io.InputStream;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;

public class SoundDetector implements AudioProcessor {

	private double threshold;
	private AudioDispatcher dispatcher;
	private SilenceDetector silenceDetector;
	private int bufferSize = 4800;
	private int overlap = 0;
	private boolean isSlience = false;

	public SoundDetector() {
		this.threshold = SilenceDetector.DEFAULT_SILENCE_THRESHOLD;
	}

	public SoundDetector(double threshold) {
		this.threshold = threshold;
	}

	public Thread getDetectorThread(InputStream inputStream) {
		UniversalAudioInputStream uaInputStream;
		TarsosDSPAudioFormat tdspFormat = new TarsosDSPAudioFormat(16000, 16, 1, true, false);
		uaInputStream = new UniversalAudioInputStream(inputStream, tdspFormat);
		dispatcher = new AudioDispatcher(uaInputStream, bufferSize, overlap);
		silenceDetector = new SilenceDetector(threshold, false);
		dispatcher.addAudioProcessor(silenceDetector);
		dispatcher.addAudioProcessor(this);
		Thread t = new Thread(dispatcher, "Audio dispatching");
		return t;
	}

	private void handleSound(AudioEvent audioEvent) {
		if (silenceDetector.currentSPL() <= threshold) {
			//System.out.println("静音： " + silenceDetector.currentSPL() + "--SampleRate--" + audioEvent.getSampleRate()
			//		+ "--Overlap--" + audioEvent.getOverlap() + "--TimeStamp--" + audioEvent.getTimeStamp());
			this.isSlience = true;
		} else {
			//System.out.println("有声，音量是：" + silenceDetector.currentSPL() + "--SampleRate--" + audioEvent.getSampleRate()
			//		+ "--Overlap--" + audioEvent.getOverlap() + "--TimeStamp--" + audioEvent.getTimeStamp());
			this.isSlience = false;
		}
	}

	@Override
	public boolean process(AudioEvent audioEvent) {
		// System.out.println("此段声音的长度是："+audioEvent.getEndTimeStamp());
		handleSound(audioEvent);
		return true;
	}

	@Override
	public void processingFinished() {
		// System.out.println("识别结束。。。。");
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public AudioDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(AudioDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public SilenceDetector getSilenceDetector() {
		return silenceDetector;
	}

	public void setSilenceDetector(SilenceDetector silenceDetector) {
		this.silenceDetector = silenceDetector;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getOverlap() {
		return overlap;
	}

	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}
	public boolean isSlience() {
		return isSlience;
	}

	public void setSlience(boolean isSlience) {
		this.isSlience = isSlience;
	}

}
