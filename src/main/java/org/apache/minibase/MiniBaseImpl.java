package org.apache.minibase;

import org.apache.minibase.DiskStore.DefaultCompactor;
import org.apache.minibase.DiskStore.MultiIter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MiniBaseImpl implements MiniBase {

  private MemStore memStore;
  private DiskStore diskStore;
  private Compactor compactor;

  private Config conf;

  public MiniBase open() throws IOException {
    assert conf != null;
    // initialize the disk store.
    diskStore = new DiskStore(conf.getDataDir(), conf.getMaxDiskFiles());
    diskStore.open();

    // initialize the memstore.
    memStore = new MemStore();

    compactor = new DefaultCompactor(diskStore);
    compactor.start();
    return this;
  }
  
  private MiniBaseImpl(Config conf) {
    this.conf = conf;
  }
  
  public static MiniBaseImpl create(Config conf) {
    return new MiniBaseImpl(conf);
  }

  public static MiniBaseImpl create() {
    return create(Config.getDefault());
  }

  @Override
  public void put(byte[] key, byte[] value) throws IOException {
    this.memStore.add(KeyValue.create(key, value));
  }

  @Override
  public byte[] get(byte[] key) throws IOException {
    // TODO
    return new byte[0];
  }

  @Override
  public void delete(byte[] key) throws IOException {
    // TODO
  }

  @Override
  public Iter<KeyValue> scan(byte[] start, byte[] stop) throws IOException {
    // TODO
    return null;
  }

  @Override
  public Iter<KeyValue> scan() throws IOException {
    List<Iter<KeyValue>> iterList = new ArrayList<>();
    iterList.add(memStore.iterator());
    iterList.add(diskStore.iterator());
    return new MultiIter(iterList);
  }

  @Override
  public void close() throws IOException {
    memStore.close();
    diskStore.close();
    compactor.interrupt();
  }
}
