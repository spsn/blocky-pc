package ge.demo.game;

public class Speed
{

	public Speed()
	{
		int xs = 128, ys = 128, zs = 128;
//		byte[][][] a = new byte[xs][ys][zs];
		byte[][] a = new byte[xs][ys];
		byte[] c = new byte[xs * ys * zs];
		int index;
		byte b;
		byte[] d;

		long st = System.currentTimeMillis();

		for (int i = 0; i < 1000; i++)
		{
			for (int j = 0; j < (xs * ys * zs); j++)
			{
//				b = c[j];
			}
		}


//		for (int i = 0; i < 1000; i++)
//		{
//			index = 0;
//
//			for (int x = 0; x < xs; x++)
//			{
//
//				for (int y = 0; y < ys; y++)
//				{
//				
//					for (int z = 0; z < zs; z++)
//					{
//						b = a[x][y];
////						d = a[x];
////1
////						b = a[x][y][z];
////2
////						b = c[0];
////3
////						b = c[(x << 8) + (y << 4) + z];
////4x
////						b = c[index];
////						index++;
//					}
//
//				}
//
//			}
//
//		}

		long et = System.currentTimeMillis();

		System.out.println(et - st);
	}

	public static void main(String[] argv)
	{
		new Speed();
	}

}
