#!/bin/bash
cd /lamp  #进入 /lamp 目录
ls *.tar.gz > ls.log     #把 ls *.tar.gz 写在一个日志文件里   >  单个表示覆盖
ls *.tgz >> ls.log       #把 ls *.taz  也写在日志里           >> 两个表示追加

for i in $(cat ls.log)   #循环日志内的 文件记录
    do 
	   tar -zxf $i       #解压日志内包名的文件
	
	done 
rm -rf ls.log            #删除临时日志文件

