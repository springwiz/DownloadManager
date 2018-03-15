/**
 * 
 */
package com.agoda.download.task;

/**
 * The Interface Task.
 *
 * @author s0n00a7
 */
public interface Task {
	/**
	 * The Enum RunStatus.
	 */
	public enum TaskStatus {

		/** The success. */
		SUCCESS("success"),

		/** The error. */
		ERROR("error"),
		
		/** The interrupted. */
		INTERRUPTED("interrupted"),
		
		/** The in progress. */
		IN_PROGRESS("inProgress"),
		
		/** The started. */
		STARTED("started");

		/** The str value. */
		private String strValue;

		/**
		 * Instantiates a new response status.
		 *
		 * @param strValue
		 *            the str value
		 */
		private TaskStatus(String strValue) {
			this.strValue = strValue;
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return strValue;
		}
	}
	
	/**
	 * Execute run.
	 *
	 * @return the run status
	 */
	public TaskStatus executeTask();

}
