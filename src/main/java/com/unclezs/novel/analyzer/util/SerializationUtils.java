package com.unclezs.novel.analyzer.util;

import com.unclezs.novel.analyzer.common.exception.UtilException;
import com.unclezs.novel.analyzer.util.io.FastByteArrayOutputStream;
import com.unclezs.novel.analyzer.util.io.IoUtils;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Object工具类
 *
 * @author blog.unclezs.com
 * @date 2021/2/15 11:01
 */
@UtilityClass
public class SerializationUtils {
  /**
   * 深克隆
   * 序列化后拷贝流的方式克隆<br>
   * 对象必须实现Serializable接口
   *
   * @param <T> 对象类型
   * @param obj 被克隆对象
   * @return 克隆后的对象
   */
  @SuppressWarnings("unchecked")
  public static <T> T deepClone(T obj) {
    if (!(obj instanceof Serializable)) {
      return null;
    }
    final FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(byteOut);
      out.writeObject(obj);
      out.flush();
      final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
      return (T) in.readObject();
    } catch (Exception e) {
      throw new UtilException(e);
    } finally {
      IoUtils.close(out);
    }
  }
}
