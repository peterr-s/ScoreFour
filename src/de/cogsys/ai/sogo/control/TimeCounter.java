package de.cogsys.ai.sogo.control;

/**
 * This class is for measurement of performance an timings. 
 * <br></br>
 * @author Sebastian Otte
 */
public class TimeCounter {
	protected long value = 0L;
	/**
	 * Resets the saved start time with the actual time.
	 */
	public void reset() {
		this.value = System.currentTimeMillis();
	}
	/**
	 * Creates an instance of PerformanceCounter.
	 */
	public TimeCounter() {
		this.reset();
	}	
   /**
     * Return the passed microseconds since the last counter reset.
     */
	public long valueMilli() {
		final long now = System.currentTimeMillis();
		return now - this.value;
	}
	

}
