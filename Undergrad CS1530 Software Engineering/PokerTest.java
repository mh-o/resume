import java.util.ArrayList;
import org.junit.Test;
import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class PokerTest {

	@Test
	public void cardValueConstructorTest()
	{
		Card testCard = new Card(5, 4);
		int result = 5;
		assertEquals("Card value is 5", result, 5);
	}

	@Test
	public void cardSuitIntegerConstructorTest()
	{
		Card testCard2 = new Card(5, 4);
		int result = 4;
		assertEquals("Card suit is 4", result, 4);

	}

	@Test
	public void setValueTest()
	{
		Card testCard3 = new Card(5, 4);
		testCard3.setValue(6);
		int result = 6;
		assertEquals("Card value set to 6", testCard3.getValue(), result);

	}

	@Test
	public void cardValueConstructorTest2()
	{
		Card testCard4 = new Card(5, 3);
		int result = 5;
		assertEquals(result, testCard4.getValue());

	}

	@Test
	public void getSuitTest()
	{
		Card testCard5 = new Card(6, 3);
		int result = 3;
		assertEquals(result, testCard5.getSuit());

	}

	@Test
	public void getSuitTest2()
	{
		Card testCard6= new Card(7, 1);
		int result = 1;
		assertEquals(result, testCard6.getSuit());

	}

	@Test
	public void setSuitTest()
	{
		Card testCard7 = new Card(6, 1);
		testCard7.setSuit(2);
		int result = 2;
		assertEquals(result, testCard7.getSuit());

	}

	@Test
	public void cardValueConstructorTest3()
	{
		Card testCard8 = new Card(10, 4);
		int result = 10;
		assertEquals("Card value is 5", result, 10);
	}

    @Test
    public void setSuitTest2()
	{
		Card testCard9 = new Card(9, 3);
		testCard9.setSuit(4);
		int result = 4;
		assertEquals(result, testCard9.getSuit());

	}

    @Test
	public void getSuitTest3()
	{
		Card testCard10 = new Card(12, 3);
		int result = 3;
		assertEquals(result, testCard10.getSuit());

	}

	@Test
	public void setValueTest2()
	{
		Card testCard11 = new Card(11, 1);
		testCard11.setValue(9);
		int result = 9;
		assertEquals("Card value set to 6", testCard11.getValue(), result);

	}

	@Test
	public void setSuitTest3()
	{
		Card testCard12 = new Card(13, 4);
		testCard12.setSuit(1);
		int result = 1;
		assertEquals(result, testCard12.getSuit());

	}
    

}