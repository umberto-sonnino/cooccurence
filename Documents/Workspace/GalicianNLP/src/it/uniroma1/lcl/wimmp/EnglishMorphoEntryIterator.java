package it.uniroma1.lcl.wimmp;

public class EnglishMorphoEntryIterator extends MorphoEntryIterator
{
	public EnglishMorphoEntryIterator(String[] dump)
	{
		super(dump);
	}
	
	/**
	 * Check if there is another entry in the raw dump
	 */
	@Override
	public boolean hasNext()
	{
		
		return false;
	}

	/**
	 * Parse the next entry and return it
	 */
	@Override
	public MorphoEntry next()
	{
		
		return null;
	}
}



