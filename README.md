
# 🪙 DhanVriddhi - A Smart Investment & Profit Sharing Platform

## 💡 Project Overview

**DhanVriddhi** is a collaborative investment management system that simplifies the process of pooling investments and ensures fair and transparent **profit distribution** among multiple investors. The platform is ideal for users looking to generate **passive income** by contributing to real-world, low-risk business ventures.

---

## 🚀 Key Features

- 📈 **Smart Profit Sharing:** Ratio-based dynamic profit calculation and distribution.
- 👥 **Multi-Investor Support:** Easily handles different investment amounts for each investor.
- 🔁 **Mid-Cycle Adjustments:** Supports joining or exiting investors mid-cycle with accurate calculations.
- 📊 **Transaction Transparency:** Integrated transaction ID tracking for secure and auditable entries.
- 🔒 **Secure Admin Controls:** Admin-restricted controls for managing daily profits, investor entries, and more.
- 🔔 **Real-time Alerts (Optional):** Twilio API integration for SMS notifications about profit disbursal and updates (can be added as enhancement).

---

## 🛠️ Tech Stack

- **Frontend:** Java AWT/Swing (can be upgraded to web or mobile UI)
- **Backend Logic:** Core Java
- **Database:** MySQL
- **Libraries/APIs:** JDBC for database integration

---

## 🧠 Core Logic

- Each investor is assigned a **weightage (investment ratio)**.
- The system tracks **daily profits** and dynamically distributes them based on active investors’ current ratios.
- For **mid-cycle joins**, the new ratio applies only from the date of entry.
- Ensures **historical consistency** by snapshotting ratios per transaction.

---

## 🔍 Example Scenario

> 💼 Investor A invests ₹10,000  
> 💼 Investor B invests ₹15,000  
> 🧮 Daily profit = ₹2,500  
> → Investor A gets ₹1,000, B gets ₹1,500 based on 40:60 ratio  
> If a new investor joins on Day 5, their profit starts getting calculated from Day 5 onward, not retroactively.

---

## 📸 Screenshots

(Add your screenshots here using Markdown if hosting on GitHub)

```markdown
![Dashboard](screenshots/dashboard.png)
![Profit Entry](screenshots/profit-entry.png)
```

---

## 🔐 Admin Features

- Add/Edit/Delete Investors
- Add Daily Profit Entry with Transaction ID
- View History of Profit Distributions
- Error Handling for Negative Entries & Validation

---

## ⚙️ How to Run

1. Clone the repository  
2. Set up MySQL DB with provided schema  
3. Run `DhanVriddhi.java` (ensure JDBC driver is linked)
4. Login using Admin credentials  
5. Start managing investor entries and profit calculations!

---

## 🔮 Future Enhancements

- ✅ Web-based Interface using React or Django  
- 📲 SMS/Email Notification System  
- 📈 Visual Analytics for Investor Earnings  
- 🧾 Exportable Reports (PDF/CSV)

---

## 👨‍💼 Developed By

**Saketh Pinumalla**  
Final Year IT Student, Vasavi College of Engineering  
🔗 _[LinkedIn, GitHub, Portfolio]_  
🔁 _Hackathon Finalist | Tech Savvy | Real-world Problem Solver_

---

## 📄 License

This project is licensed under the MIT License.
