# Giả lập luồng import file csv, lưu vào DB, index vào elasticsearch của TLA

## Luồng cơ bản:
1. Import csv
2. Lưu vào DB
3. Index elasticsearch


### Chi tiết luồng 3 : index elasticsearch, Cơ chế handle fallback khi service elasticsearch lỗi:

 - ![img.png](src/img.png)
 - ![img_1.png](src/img_1.png)

# Test
1. Import data
 - ![img.png](img.png)
 - Output:
 - ![img_1.png](img_1.png)

2. Index solr, elasticsearch
 - Output:
 - ![img_2.png](img_2.png)