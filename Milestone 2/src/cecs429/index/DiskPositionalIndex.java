package cecs429.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cecs429.database.DatabaseUtil;
import edu.csulb.BetterTermDocumentIndexerWithPositionalInvertedIndex;

public class DiskPositionalIndex implements Index {
	private List<String> mVocabulary;

	public DiskPositionalIndex(List<String> mVocabulary, String diskIndexPath) {
		this();
		this.mVocabulary = mVocabulary;
	}

	private static RandomAccessFile positionsInput = null;
	private static RandomAccessFile docWeightsIinput = null;
	private static RandomAccessFile soundexInput = null;
	private static RandomAccessFile biwordInput = null;
	private static DatabaseUtil databaseUtil = null;

	public DiskPositionalIndex() {
		super();
		if (positionsInput == null) {
			try {
				positionsInput = new RandomAccessFile(
						new File(BetterTermDocumentIndexerWithPositionalInvertedIndex.diskIndexPath), "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (docWeightsIinput == null) {
			try {
				docWeightsIinput = new RandomAccessFile(
						new File(BetterTermDocumentIndexerWithPositionalInvertedIndex.docWeightsFile), "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (soundexInput == null) {
			try {
				soundexInput = new RandomAccessFile(
						new File(BetterTermDocumentIndexerWithPositionalInvertedIndex.soundexFile), "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (biwordInput == null) {
			try {
				biwordInput = new RandomAccessFile(
						new File(BetterTermDocumentIndexerWithPositionalInvertedIndex.biwordIndexFile), "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (databaseUtil == null) {
			databaseUtil = new DatabaseUtil();
		}
	}

	@Override
	public List<Posting> getPostings(String term) {
		byte[] bs = new byte[4];
		List<Posting> termPostigs = new ArrayList<>();
		try {
			int termIndexPosition = databaseUtil.getTermIndexFromDB(term);
			if (termIndexPosition > 0) {
				positionsInput.seek(termIndexPosition);
			}
			int numberOfPostings = readInteger(bs, positionsInput);
			for (int i = 0; i < numberOfPostings; i++) {
				int documentId = -1;
				if (i == 0) {
					documentId = readInteger(bs, positionsInput);
				} else {
					int temp = readInteger(bs, positionsInput);
					documentId = termPostigs.get(termPostigs.size() - 1).getDocumentId() + temp;
				}
				Posting posting = new Posting(documentId);
				int freq = readInteger(bs, positionsInput);
				List<Integer> positions = new ArrayList<>();
				for (int j = 0; j < freq; j++) {
					int pos = readInteger(bs, positionsInput);
					if (j == 0) {
						positions.add(pos);
					} else {
						positions.add(positions.get(j - 1) + pos);
					}
				}
				posting.setMtftd(freq);
				posting.setPositions(positions);
				termPostigs.add(posting);
			}

		} catch (FileNotFoundException e) {
			System.out.println("Unable to find index file.");
		} catch (IOException e) {
			System.out.println("Unable to read the file.");
		}
		return termPostigs;
	}

	private int readInteger(byte[] bs, RandomAccessFile dataInputStream) throws IOException {
		int readBytes = dataInputStream.read(bs);
		if (readBytes > 0) {
			return ByteBuffer.wrap(bs).getInt();
		}
		return -1;
	}

	@Override
	public List<String> getVocabulary() {
		return mVocabulary;
	}

	public double getLd(int documentId) {
		double ld = -1;
		byte[] bs = new byte[8];
		try {
			int docDataPosition = databaseUtil.getDocWeightsFromDB(documentId);
			int skipBytes = docDataPosition;
			if (skipBytes > 0) {
				docWeightsIinput.seek(skipBytes);
			}
			ld = readDouble(bs, docWeightsIinput);
		} catch (Exception e) {
		}
		return ld;
	}

	private double readDouble(byte[] bs, RandomAccessFile dataInputStream) throws IOException {
		int readBytes = dataInputStream.read(bs);
		if (readBytes > 0) {
			return ByteBuffer.wrap(bs).getDouble();
		}
		return -1;
	}

	@Override
	public List<Posting> getPostingsWithoutPositions(String term) {
		List<Posting> postings = new ArrayList<>();
		byte[] bs = new byte[4];
		try {
			int termIndexPosition = databaseUtil.getTermIndexFromDB(term);
			if (termIndexPosition > 0) {
				positionsInput.seek(termIndexPosition);
			}
			int numberOfPostings = readInteger(bs, positionsInput);
			for (int i = 0; i < numberOfPostings; i++) {
				int documentId = -1;
				if (i == 0) {
					documentId = readInteger(bs, positionsInput);
				} else {
					int temp = readInteger(bs, positionsInput);
					documentId = postings.get(postings.size() - 1).getDocumentId() + temp;
				}
				Posting posting = new Posting(documentId);
				int freq = readInteger(bs, positionsInput);
				posting.setMtftd(freq);
				postings.add(posting);
				for (int j = 0; j < freq; j++) {
					readInteger(bs, positionsInput);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find index file.");
		} catch (IOException e) {
			System.out.println("Unable to read the file.");
		}
		return postings;
	}

	@Override
	public List<Integer> getSoundex(String term) {
		List<Integer> docIds = new ArrayList<>();
		byte[] bs = new byte[4];
		try {
			int termIndexPosition = databaseUtil.getSoundexFromDB(term);
			if (termIndexPosition == -1) {
				return docIds;
			}
			if (termIndexPosition > 0) {
				soundexInput.seek(termIndexPosition);
			}
			int freq = readInteger(bs, soundexInput);
			for (int i = 0; i < freq; i++) {
				int documentId = -1;
				if (i == 0) {
					documentId = readInteger(bs, soundexInput);
				} else {
					int temp = readInteger(bs, soundexInput);
					documentId = docIds.get(i - 1) + temp;
				}
				docIds.add(documentId);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Please check for file or change corpus to run the soundex query");
		} catch (IOException e) {
			System.out.println("Unable to read the file.");
		}
		return docIds;
	}

	@Override
	public List<Integer> getBiwordPostings(String term) {
		List<Integer> docIds = new ArrayList<>();
		byte[] bs = new byte[4];
		try {
			int termIndexPosition = databaseUtil.getBiwordIndexFromDB(term);
			if (termIndexPosition == -1) {
				return docIds;
			}
			if (termIndexPosition > 0) {
				biwordInput.seek(termIndexPosition);
			}
			int freq = readInteger(bs, biwordInput);
			for (int i = 0; i < freq; i++) {
				int documentId = -1;
				if (i == 0) {
					documentId = readInteger(bs, biwordInput);
				} else {
					int temp = readInteger(bs, biwordInput);
					documentId = docIds.get(i - 1) + temp;
				}
				docIds.add(documentId);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find index file.");
		} catch (IOException e) {
			System.out.println("Unable to read the file.");
		}
		return docIds;
	}

	public double getavgTftd(int docID) throws IOException {
		double tftd = -1;
		byte[] bs = new byte[8];
		try {
			int docDataPosition = databaseUtil.getDocWeightsFromDB(docID);
			int skipBytes = docDataPosition + 24;
			if (skipBytes > 0) {
				docWeightsIinput.seek(skipBytes);
			}
			tftd = readDouble(bs, docWeightsIinput);
		} catch (Exception e) {
		}
		return tftd;
	}

	public double getdocLength(int docID) throws IOException {
		double docLength = -1;
		byte[] bs = new byte[8];
		try {
			int docDataPosition = databaseUtil.getDocWeightsFromDB(docID);
			int skipBytes = docDataPosition + 8;
			if (skipBytes > 0) {
				docWeightsIinput.seek(skipBytes);
			}
			docLength = readDouble(bs, docWeightsIinput);
		} catch (Exception e) {
		}
		return docLength;
	}

	public double getbyteSize(int docID) throws IOException {
		double byteSize = -1;
		byte[] bs = new byte[8];
		try {
			int docDataPosition = databaseUtil.getDocWeightsFromDB(docID);
			int skipBytes = docDataPosition + 16;
			if (skipBytes > 0) {
				docWeightsIinput.seek(skipBytes);
			}
			byteSize = readDouble(bs, docWeightsIinput);
		} catch (Exception e) {
		}
		return byteSize;
	}

	public double getdocLengthA() throws IOException {
		long position = docWeightsIinput.length() - 8;
		docWeightsIinput.seek(position);
		byte[] byteBuffer = new byte[8];
		docWeightsIinput.read(byteBuffer, 0, byteBuffer.length);
		ByteBuffer wrapped = ByteBuffer.wrap(byteBuffer);
		double docLengthA = wrapped.getDouble();
		return docLengthA;
	}
}