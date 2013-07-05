using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Windows.Forms;

static class Serializer {
    // Serialize an object
    static public void SerializeObject<T>(T serializableObject, string fileName) {
        if (serializableObject == null) { return; }

        try {
            Stream stream = File.Open(fileName, FileMode.Create);
            BinaryFormatter bformatter = new BinaryFormatter();

            bformatter.Serialize(stream, serializableObject);
            stream.Close();
        } catch (Exception ex) {
            MessageBox.Show("Could not save data to disk. " + ex.Message);
        }
    }


    // Deserilize an object
    static public T DeSerializeObject<T>(string fileName) {
        if (string.IsNullOrEmpty(fileName)) { return default(T); }

        T objectOut = default(T);
        
        try {
            BinaryFormatter bformatter = new BinaryFormatter();
            Stream stream = File.Open(fileName, FileMode.Open);
            bformatter = new BinaryFormatter();
        
            objectOut = (T) bformatter.Deserialize(stream);
            stream.Close();
        } catch (Exception ex) {
            MessageBox.Show("Could not retrieve data from xml." + ex.Message);
        }

        return objectOut;
    }
}
