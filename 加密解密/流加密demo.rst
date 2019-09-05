*****
流加密样例
*****


.. code-block:: java


	/**
	AES 的分组加解密存在多种模式，可以分成2个大类：分组独立和分组依赖。

	分组独立：各个分组之间可以独立的加解密，支持并发。
	分组依赖：下一个分组的加解密依赖于上一个分组的输出，也就不支持并发。
	 */
	public class TestAes {
	    private int bufferSize = 2048;
	    private int maxSize = 2048 * 1024;

	    public static void main(String[] args) throws Exception {
	        TestAes testAes = new TestAes();
	        byte[] seed = "helloworld".getBytes("utf-8");
	        byte[] key = testAes.getRawKey(seed);
	        byte[] iv = "1234567890123456".getBytes("utf-8");
	        String content = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
	        testAes.encrypt(iv, key, new ByteArrayInputStream(content.getBytes("utf-8")));
	        String file = System.getProperty("user.home") + File.separator + "cipher.txt";
	        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
	        testAes.decrypt(iv, key, inputStream);
	    }

	    private void encrypt(byte[] iv, byte[] key, InputStream inputStream) throws Exception {
	        IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
	        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
	        byte[] buffer = new byte[bufferSize]; // 一次加密块大小
	        List<byte[]> container = new ArrayList<>();
	        int bytesRead;
	        int limit = maxSize;
	        boolean finish = false;

	        while ((bytesRead = IOUtils.read(inputStream, buffer)) != 0) {
	            limit -= bytesRead;
	            if (bytesRead == bufferSize) {
	                container.add(cipher.update(buffer));
	            }
	            else { // 数据达不到最大块就到末尾了
	                container.add(cipher.doFinal(buffer, 0, bytesRead));
	                finish = true;
	            }
	            if (limit == 0) { // 当到达加密的最大块时，开始传输加密数据
	                handleCipher(container, finish);
	                container = new ArrayList<>(); //清空容器
	                limit = maxSize; // 重置限制为最大块
	            }
	        }

	        if (!finish) { // 如果数据块刚好为bufferSize整数倍
	            container.add(cipher.doFinal());
	            finish = true;
	        }
	        if (container.size() > 0) // 清空容器内的加密数据
	            handleCipher(container, finish);
	    }

	    private void decrypt(byte[] iv, byte[] key, InputStream inputStream) throws Exception{
	        IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
	        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
	        byte[] buffer = new byte[bufferSize];
	        List<byte[]> container = new ArrayList<>();
	        int bytesRead;
	        int limit = maxSize;
	        boolean finish = false;

	        while ((bytesRead = IOUtils.read(inputStream, buffer)) != 0) {
	            limit -= bytesRead;
	            if (bytesRead == bufferSize) {
	                container.add(cipher.update(buffer));
	            }
	            else {
	                container.add(cipher.doFinal(buffer, 0, bytesRead));
	                finish = true;
	            }
	            if (limit == 0) {
	                handlePlain(container, finish);
	                container = new ArrayList<>();
	                limit = maxSize;
	            }
	        }

	        if (!finish) {
	            container.add(cipher.doFinal());
	            finish = true;
	        }
	        if (container.size() > 0)
	            handlePlain(container, finish);
	    }

	    //加密函数回调此方法，当内容到达最大值或者流结束时调用
	    private void handleCipher(List<byte[]> list, boolean finish) throws Exception {
	        File file = new File(System.getProperty("user.home") + File.separator + "cipher.txt");
	        if (!file.exists()) {
	            file.createNewFile();
	        }
	        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, true));
	        list.forEach(item -> {
	            System.out.println(item.length);
	            System.out.println(Arrays.toString(item));
	            try {
	                outputStream.write(item);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        });
	        if (finish)
	            outputStream.flush();
	        outputStream.close();
	    }

	    //解密函数回调此方法，当内容到达最大值或者流结束时调用
	    private void handlePlain(List<byte[]> list, boolean finish) throws Exception{
	        File file = new File(System.getProperty("user.home") + File.separator + "plain.txt");
	        if (!file.exists()) {
	            file.createNewFile();
	        }
	        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, true));
	        list.forEach(item -> {
	            System.out.println(item.length);
	            System.out.println(Arrays.toString(item));
	            try {
	                outputStream.write(item);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        });
	        if (finish)// 当流结束，则立即发送流
	            outputStream.flush();
	        outputStream.close();
	    }

	    //生成随机密钥
	    private byte[] getRawKey(byte[] seed) throws Exception {
	        KeyGenerator kgen = KeyGenerator.getInstance("AES");
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	        sr.setSeed(seed);
	        kgen.init(128, sr);
	        SecretKey skey = kgen.generateKey();
	        byte[] raw = skey.getEncoded();
	        return raw;
	    }
	}



