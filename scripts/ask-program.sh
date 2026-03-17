curl http://localhost:8080/web/ask \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"title": "JAVA", "question": "给定一个非递减排序的整数数组 nums 和一个目标值 target，请编写一个函数，返回 target 在数组中出现的第一个位置和最后一个位置（下标从 0 开始）。​\n  - 如果 target 未在数组中出现，返回 [-1, -1]；​\n  - 要求：时间复杂度不超过 O(logn)，空间复杂度 O(1)。​\n示例​\n  1. 输入：nums = [5,7,7,8,8,10], target = 8 → 输出：[3,4]​\n  2. 输入：nums = [5,7,7,8,8,10], target = 6 → 输出：[-1,-1]​\n  3. 输入：nums = [], target = 0 → 输出：[-1,-1]​\n  4. 输入：nums = [2,2], target = 2 → 输出：[0,1]"}' 
