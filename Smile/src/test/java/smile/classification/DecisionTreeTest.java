/******************************************************************************
 *                   Confidential Proprietary                                 *
 *         (c) Copyright Haifeng Li 2011, All Rights Reserved                 *
 ******************************************************************************/
package smile.classification;

import smile.sort.QuickSort;
import smile.data.Attribute;
import smile.data.NominalAttribute;
import smile.data.parser.DelimitedTextParser;
import smile.validation.LOOCV;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.math.Math;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Haifeng
 */
public class DecisionTreeTest {
    
    public DecisionTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Test of learn method, of class DecisionTree.
     */
    @Test
    public void testWeather() {
        System.out.println("Weather");
        ArffParser arffParser = new ArffParser();
        arffParser.setResponseIndex(4);
        try {
            AttributeDataset weather = arffParser.parse(this.getClass().getResourceAsStream("/smile/data/weka/weather.nominal.arff"));
            double[][] x = weather.toArray(new double[weather.size()][]);
            int[] y = weather.toArray(new int[weather.size()]);

            int n = x.length;
            LOOCV loocv = new LOOCV(n);
            int error = 0;
            for (int i = 0; i < n; i++) {
                double[][] trainx = Math.slice(x, loocv.train[i]);
                int[] trainy = Math.slice(y, loocv.train[i]);
                
                DecisionTree tree = new DecisionTree(weather.attributes(), trainx, trainy, 3);
                if (y[loocv.test[i]] != tree.predict(x[loocv.test[i]]))
                    error++;
            }
            
            System.out.println("Decision Tree error = " + error);
            assertEquals(5, error);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Test of learn method, of class DecisionTree.
     */
    @Test
    public void testIris() {
        System.out.println("Iris");
        ArffParser arffParser = new ArffParser();
        arffParser.setResponseIndex(4);
        try {
            AttributeDataset iris = arffParser.parse(this.getClass().getResourceAsStream("/smile/data/weka/iris.arff"));
            double[][] x = iris.toArray(new double[iris.size()][]);
            int[] y = iris.toArray(new int[iris.size()]);

            int n = x.length;
            LOOCV loocv = new LOOCV(n);
            int error = 0;
            for (int i = 0; i < n; i++) {
                double[][] trainx = Math.slice(x, loocv.train[i]);
                int[] trainy = Math.slice(y, loocv.train[i]);
                
                DecisionTree tree = new DecisionTree(iris.attributes(), trainx, trainy, 4);
                if (y[loocv.test[i]] != tree.predict(x[loocv.test[i]]))
                    error++;
            }
            
            System.out.println("Decision Tree error = " + error);
            assertEquals(7, error);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Test of learn method, of class DecisionTree.
     */
    @Test
    public void testUSPS() {
        System.out.println("USPS");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setResponseIndex(new NominalAttribute("class"), 0);
        try {
            AttributeDataset train = parser.parse("USPS Train", this.getClass().getResourceAsStream("/smile/data/usps/zip.train"));
            AttributeDataset test = parser.parse("USPS Test", this.getClass().getResourceAsStream("/smile/data/usps/zip.test"));

            double[][] x = train.toArray(new double[train.size()][]);
            int[] y = train.toArray(new int[train.size()]);
            double[][] testx = test.toArray(new double[test.size()][]);
            int[] testy = test.toArray(new int[test.size()]);
            
            DecisionTree tree = new DecisionTree(x, y, 350, DecisionTree.SplitRule.ENTROPY);
            
            int error = 0;
            for (int i = 0; i < testx.length; i++) {
                if (tree.predict(testx[i]) != testy[i]) {
                    error++;
                }
            }

            System.out.format("USPS error rate = %.2f%%\n", 100.0 * error / testx.length);
            assertEquals(328, error);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Test of learn method, of class DecisionTree.
     */
    @Test
    public void testUSPSNominal() {
        System.out.println("USPS nominal");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setResponseIndex(new NominalAttribute("class"), 0);
        try {
            AttributeDataset train = parser.parse("USPS Train", this.getClass().getResourceAsStream("/smile/data/usps/zip.train"));
            AttributeDataset test = parser.parse("USPS Test", this.getClass().getResourceAsStream("/smile/data/usps/zip.test"));

            double[][] x = train.toArray(new double[train.size()][]);
            int[] y = train.toArray(new int[train.size()]);
            double[][] testx = test.toArray(new double[test.size()][]);
            int[] testy = test.toArray(new int[test.size()]);
            
            for (double[] xi : x) {
                for (int i = 0; i < xi.length; i++) {
                    xi[i] = Math.round(255*(xi[i]+1)/2);
                }
            }
            
            for (double[] xi : testx) {
                for (int i = 0; i < xi.length; i++) {
                    xi[i] = Math.round(127 + 127*xi[i]);
                }
            }
            
            Attribute[] attributes = new Attribute[256];
            String[] values = new String[attributes.length];
            for (int i = 0; i < attributes.length; i++) {
                values[i] = String.valueOf(i);
            }
            
            for (int i = 0; i < attributes.length; i++) {
                attributes[i] = new NominalAttribute("V"+i, values);
            }
            
            DecisionTree tree = new DecisionTree(attributes, x, y, 350, DecisionTree.SplitRule.ENTROPY);
            
            int error = 0;
            for (int i = 0; i < testx.length; i++) {
                if (tree.predict(testx[i]) != testy[i]) {
                    error++;
                }
            }

            System.out.format("USPS error rate = %.2f%%\n", 100.0 * error / testx.length);
            
            double[] importance = tree.importance();
            int[] index = QuickSort.sort(importance);
            for (int i = importance.length; i-- > 0; ) {
                System.out.format("%s importance is %.4f\n", train.attributes()[index[i]], importance[i]);
            }
            
            assertEquals(337, error);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
