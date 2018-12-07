using System;
using System.ComponentModel;
using System.Numerics;
using Ont.SmartContract.Framework;
using Ont.SmartContract.Framework.Services.Ont;
using Ont.SmartContract.Framework.Services.System;

namespace Ontology
{
    public class Ontology : SmartContract
    {
        public delegate void PutRecord(string operation, byte[] key, byte[] value);
        public delegate void GetRecord(string operation, byte[] key);

        [DisplayName("putRecord")]
        public static event PutRecord PutEvent;

        [DisplayName("getRecord")]
        public static event GetRecord GetEvent;

        public static Object Main(string operation, params object[] args)
        {
            if (operation == "PutScope")
            {
                if (args.Length != 2) return false;
                byte[] key = (byte[])args[0];
                byte[] value = (byte[])args[1];
                return PutScope(key, value);
            }

            if (operation == "GetScope")
            {
                if (args.Length != 1) return false;
                byte[] key = (byte[])args[0];
                return GetScope(key);
            }
            return false;
        }

        public static bool PutScope(byte[] key, byte[] value)
        {
            Storage.Put(Storage.CurrentContext, key, value);
            PutEvent("PutScope", key, value);
            Runtime.Notify("Put a scope");
            return true;
        }

        public static byte[] GetScope(byte[] key)
        {
            GetEvent("GetScope", key);
            Runtime.Notify("Get a scope view");
            return Storage.Get(Storage.CurrentContext, key);
        }
    }
}