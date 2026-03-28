import subprocess
import os

maven_cmd = r"C:\Users\kamal\OneDrive\Desktop\hussain s6\apache-maven-3.9.6\bin\mvn.cmd"
try:
    result = subprocess.run([maven_cmd, "clean", "test"], capture_output=True, text=True, cwd=r"C:\Users\kamal\OneDrive\Desktop\hussain s6", shell=True)
    with open("py-out.txt", "w") as f:
        f.write("STDOUT:\n")
        f.write(result.stdout)
        f.write("STDERR:\n")
        f.write(result.stderr)
        f.write(f"\nRETURN CODE: {result.returncode}\n")
except Exception as e:
    with open("py-out.txt", "w") as f:
        f.write(f"ERROR: {e}")
