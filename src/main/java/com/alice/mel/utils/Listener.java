package com.alice.mel.utils;

public interface Listener<T> {
	/**
	 * @param signal The Signal that triggered event
	 * @param object The object passed on dispatch
	 */
	public void receive (Signal<T> signal, T object);
}