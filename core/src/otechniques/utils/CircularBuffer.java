package otechniques.utils;

import java.util.Iterator;

public class CircularBuffer<T> implements Iterable<T> {
	private T[] items;
	private int tail;
	private int size;

	@SuppressWarnings("unchecked")
	public CircularBuffer(int capacity) {
		this.items = (T[]) new Object[capacity];
		this.size = capacity;
		this.tail = 0;
	}

	/**
	 * Adds the given item to the tail of this circular buffer.
	 * 
	 * @param item
	 *            the item to add
	 * @return {@code true} if the item has been successfully added to this
	 *         circular buffer; {@code false} otherwise.
	 */
	public boolean put(T item) {
		tail = (tail + 1) % size;
		items[tail] = item;
		return true;
	}

	/**
	 * Removes and returns the item at the head of this circular buffer (if
	 * any).
	 * 
	 * @return the item just removed or {@code null} if this circular buffer is
	 *         empty.
	 */
	public T getFirst() {
		return items[tail];
	}

	public T getLast() {
		return items[(tail + 1) % size];
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> it = new Iterator<T>() {

			// oldest elemnt first
			private int currentIndex = (tail + 1) % size;

			@Override
			public boolean hasNext() {
				return currentIndex != tail;
			}

			@Override
			public T next() {
				currentIndex = (currentIndex + 1) % size;
				return items[currentIndex];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}
	
}
