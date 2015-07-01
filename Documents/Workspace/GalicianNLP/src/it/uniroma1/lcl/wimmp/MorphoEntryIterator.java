package it.uniroma1.lcl.wimmp;

import java.util.Iterator;

abstract public class MorphoEntryIterator implements Iterator<MorphoEntry>
{
	/**
	 * Array of dump file names
	 */
	private String[] dumps;
	
	public MorphoEntryIterator(String[] dumps)
	{
		this.dumps = dumps;
	}
	
	/**
	 * Returns the array of one or two Wiktionary dump file names
	 * @return array of dump file names
	 */
	public String[] getDumps() { return dumps; }
	
	/**
	 * Do not implement
	 */
	@Override
	public void remove()
	{
		new RuntimeException("Not implemented");
	}
}
