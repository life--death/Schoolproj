package edu.uci.ics.textdb.exp.nlp.sentiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.uci.ics.textdb.api.exception.TextDBException;
import edu.uci.ics.textdb.api.tuple.Tuple;
import edu.uci.ics.textdb.exp.sink.tuple.TupleSink;
import edu.uci.ics.textdb.exp.source.tuple.TupleSourceOperator;

public class NltkSentimentOperatorTest {
    private static String NEGATIVE_CLASS_LABEL = "neg";
    private static String POSITIVE_CLASS_LABEL = "pos";
    private static String MODEL_FILE_NAME = "NltkSentiment.pickle";
    private static int BATCH_SIZE = 1000;
    /*
     * Test sentiment test result should be positive.
     */
    @Test
    public void test1() throws TextDBException {
        TupleSourceOperator tupleSource = new TupleSourceOperator(
                Arrays.asList(NltkSentimentTestConstants.POSITIVE_TUPLE), NlpSentimentTestConstants.SENTIMENT_SCHEMA);
        NltkSentimentOperator nltkSentimentOperator = new NltkSentimentOperator(
                new NltkSentimentOperatorPredicate(NlpSentimentTestConstants.TEXT, "sentiment", BATCH_SIZE, MODEL_FILE_NAME));
        TupleSink tupleSink = new TupleSink();
        
        nltkSentimentOperator.setInputOperator(tupleSource);
        tupleSink.setInputOperator(nltkSentimentOperator);
        
        tupleSink.open();
        List<Tuple> results = tupleSink.collectAllTuples();
        tupleSink.close();
        
        Tuple tuple = results.get(0);
        Assert.assertEquals(tuple.getField("sentiment").getValue(), POSITIVE_CLASS_LABEL);
    }
    
    /*
     * Test sentiment test result should be negative
     */
    @Test
    public void test2() throws TextDBException {
        TupleSourceOperator tupleSource = new TupleSourceOperator(
                Arrays.asList(NltkSentimentTestConstants.NEGATIVE_TUPLE), NlpSentimentTestConstants.SENTIMENT_SCHEMA);
        NltkSentimentOperator nltkSentimentOperator = new NltkSentimentOperator(
                new NltkSentimentOperatorPredicate(NltkSentimentTestConstants.TEXT, "sentiment", BATCH_SIZE, MODEL_FILE_NAME));
        
        TupleSink tupleSink = new TupleSink();
        
        nltkSentimentOperator.setInputOperator(tupleSource);
        tupleSink.setInputOperator(nltkSentimentOperator);
        
        tupleSink.open();
        List<Tuple> results = tupleSink.collectAllTuples();
        tupleSink.close();
        
        Tuple tuple = results.get(0);
        Assert.assertEquals(tuple.getField("sentiment").getValue(), NEGATIVE_CLASS_LABEL);        
    }
    
    /*
     * Test batch processing of operator. All test results should be negative
     */
    @Test
    public void test3() throws TextDBException {
        int bufferSize = 30;
        int tupleSize = 101;
        
        List<Tuple> listTuple = new ArrayList<>();
        for (int i=0; i<tupleSize; i++) {
            listTuple.add(NltkSentimentTestConstants.NEGATIVE_TUPLE);
        }
        TupleSourceOperator tupleSource = new TupleSourceOperator(
                listTuple, NltkSentimentTestConstants.SENTIMENT_SCHEMA);
        NltkSentimentOperator nltkSentimentOperator = new NltkSentimentOperator(
                new NltkSentimentOperatorPredicate(NlpSentimentTestConstants.TEXT, "sentiment", bufferSize, MODEL_FILE_NAME));
        
        TupleSink tupleSink = new TupleSink();
        
        nltkSentimentOperator.setInputOperator(tupleSource);
        tupleSink.setInputOperator(nltkSentimentOperator);
        
        tupleSink.open();
        List<Tuple> results = tupleSink.collectAllTuples();
        tupleSink.close();
        for (int j=0; j<tupleSize; j++) {
            Tuple tuple = results.get(j);
            Assert.assertEquals(tuple.getField("sentiment").getValue(), NEGATIVE_CLASS_LABEL);
        }
    }

}
