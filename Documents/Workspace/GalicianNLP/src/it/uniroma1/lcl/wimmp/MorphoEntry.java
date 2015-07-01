package it.uniroma1.lcl.wimmp;


public class MorphoEntry
{
	public enum POS
	{
		NOUN("n"),
		VERB("v"),
		ADJECTIVE("a"),
		ADVERB("r");
		
		private String pos;
		
		POS(String pos) { this.pos = pos; }
		public String toString() { return pos; }
	}
	
	private String lemma;
	private POS partOfSpeech;
	private boolean regular;
	private MorphoRule rule;
	
	public MorphoEntry(String lemma, POS partOfSpeech, boolean regular, MorphoRule rule)
	{
		this.lemma = lemma;
		this.partOfSpeech = partOfSpeech;
		this.regular = regular;
		this.rule = rule;
	}
	
	public String getLemma(){ return lemma; }
	public POS getPOS() {return partOfSpeech;}
	public boolean isRegular() {return regular;}
	public MorphoRule getRule() {return rule;}
}
