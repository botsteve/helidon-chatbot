
#Run the process with memory limit on low memory systems(need swap file setup)
systemd-run --scope -p MemoryLimit=500M java -Dserver.port=8080 -Dllm.path=/root/ggml-gpt4all-j-v1.3-groovy.bin -cp 'chat.jar':'libs/*' com.gpt.chat.GPTChat