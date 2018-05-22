import static org.junit.Assert.*;

import org.junit.Test;


public class RuleTest {

	@Test
	public void testIssame() {
		//���Գ����Ƿ���ͬ
		String []x={"d4","s4"};
		String []y={"d4","s4"};
		assertEquals(true, Rule.Issame(x, y));
		
		x=new String[]{"d5","s5"};
		y=new String[]{"d4","s4"};
		assertEquals(false, Rule.Issame(x, y));
	}

	@Test
	public void testIsregularStringArray() {
		//���ƹ���
		//����
		String []x={"d6","s6"};
		assertEquals(true, Rule.Isregular(x));
		
		//������
		x=new String[]{"d6","s6","h6"};
		assertEquals(true, Rule.Isregular(x));
		
		//����һ
		x=new String[]{"d6","s6","h6","s3"};
		assertEquals(true, Rule.Isregular(x));
		
		//������
		x=new String[]{"d6","s6","h6","s3","h3"};
		assertEquals(true, Rule.Isregular(x));
		
		//������
		x=new String[]{"d7","s7","d6","s6","h5","s5"};
		assertEquals(true, Rule.Isregular(x));
		
				
		//4����
		x=new String[]{"d8","s8","d7","s7","d6","s6","h5","s5"};
		assertEquals(true, Rule.Isregular(x));
		
		//5����
		x=new String[]{"d9","s9","d8","s8","d7","s7","d6","s6","h5","s5"};
		assertEquals(true, Rule.Isregular(x));
		
		
		//5˳��
		x=new String[]{"d9","s8","h7","s6","s5"};
		assertEquals(true, Rule.Isregular(x));
		
		//6˳��
		x=new String[]{"d13","d12","s11","h10","s9","s8"};
		assertEquals(true, Rule.Isregular(x));
	
		
		//7˳��
		x=new String[]{"d13","d12","s11","h10","s9","s8","s7"};
		assertEquals(true, Rule.Isregular(x));
		
		//ը��
		x=new String[]{"d8","s8","h8","d8"};
		assertEquals(true, Rule.Isregular(x));
	
		//��ը
		x=new String[]{"s16","s17"};
		assertEquals(true, Rule.Isregular(x));
	
		
	}

	@Test
	public void testIsregularStringArrayStringArray() {
		//����
		String []x={"d6","s6"};
		String []y={"c5","d5"};
		//Rule.Isregular(x, y);
		assertEquals(true, Rule.Isregular(x, y));
		
		//������
		x=new String[]{"d6","s6","h6"};
		y=new String[]{"d5","s5","h5"};
		assertEquals(true, Rule.Isregular(x, y));
		
		
		//����һ
		x=new String[]{"d6","s6","h6","s3"};
		y=new String[]{"d5","s5","h5","s7"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//5˳��
		x=new String[]{"d12","s11","h10","s9","s8"};
		y=new String[]{"d8","d7","s6","h5","s4"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//6˳��
		x=new String[]{"d13","d12","s11","h10","s9","s8"};
		y=new String[]{"d9","d8","d7","s6","h5","s4"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//7˳��
		x=new String[]{"d13","d12","s11","h10","s9","s8","s7"};
		y=new String[]{"d9","d8","d7","s6","h5","s4","s3"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//������
		x=new String[]{"d6","s6","h6","s3","h3"};
		y=new String[]{"d5","s5","h5","s7","h7"};
		assertEquals(true, Rule.Isregular(x, y));
		
		
		
		//������
		x=new String[]{"d7","s7","d6","s6","h5","s5"};
		y=new String[]{"d5","s5","h4","s4","h3","s3"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//4����
		x=new String[]{"d8","s8","d7","s7","d6","s6","h5","s5"};
		y=new String[]{"d6","s6","d5","s5","h4","s4","h3","s3"};
		assertEquals(true, Rule.Isregular(x, y));
		
		
		//�ɻ�
		x=new String[]{"d8","s8","h8","d7","h7","s7"};
		y=new String[]{"d6","s6","h6","d5","s5","h5"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//ը��ѹ�ɻ�
		x=new String[]{"d8","s8","h8","d8"};
		y=new String[]{"d6","s6","h6","d5","s5","h5"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//ը��ѹ˳��
		x=new String[]{"d8","s8","h8","d8"};
		y=new String[]{"d8","d7","s6","h5","s4"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//ը��ѹը��
		x=new String[]{"d8","s8","h8","d8"};
		y=new String[]{"d7","d7","s7","h7"};
		assertEquals(true, Rule.Isregular(x, y));
		
		//��ըѹը��
		x=new String[]{"s16","s17"};
		y=new String[]{"c15","d15","s15","h15"};
		assertEquals(true, Rule.Isregular(x, y));
	}




}
