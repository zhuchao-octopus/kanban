package pm;
public interface IPackageInstallObserver extends android.os.IInterface {

    public abstract static class Stub extends android.os.Binder implements pm.IPackageInstallObserver {
        public Stub() {
            throw new RuntimeException("Stub!");
        }

        public static pm.IPackageInstallObserver asInterface(android.os.IBinder obj) {
            throw new RuntimeException("Stub!");
        }

        public android.os.IBinder asBinder() {
            throw new RuntimeException("Stub!");
        }

        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
                throws android.os.RemoteException {
            throw new RuntimeException("Stub!");
        }
    }

    public abstract void packageInstalled(java.lang.String packageName, int returnCode)
            throws android.os.RemoteException;
}
