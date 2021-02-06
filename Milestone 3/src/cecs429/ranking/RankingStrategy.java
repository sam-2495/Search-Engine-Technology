package cecs429.ranking;
public interface RankingStrategy {
    double getWQT(double N, double dft);
    double getWDT(double tftd, int docID);
    double getLD(int docId);
    
}