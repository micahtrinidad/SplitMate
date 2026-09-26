import { useEffect, useState } from 'react'

const userId = 2
const expensesUrl = `http://localhost:8080/users/${userId}/expenses`

async function fetchExpenses() {
  const response = await fetch(expensesUrl)

  if (!response.ok) {
    throw new Error('Unable to load expenses.')
  }

  return response.json()
}

function App() {
  const [expenses, setExpenses] = useState([])
  const [categories, setCategories] = useState([])
  const [description, setDescription] = useState('')
  const [amount, setAmount] = useState('')
  const [date, setDate] = useState('')
  const [selectedCategoryId, setSelectedCategoryId] = useState('')
  const [submitError, setSubmitError] = useState('')

  useEffect(() => {
    fetchExpenses()
      .then(setExpenses)
      .catch(error => console.error(error))

    fetch('http://localhost:8080/categories')
      .then(response => {
        if (!response.ok) {
          throw new Error('Unable to load categories.')
        }

        return response.json()
      })
      .then(setCategories)
      .catch(error => console.error(error))
  }, [])

  async function handleSubmit(event) {
    event.preventDefault()
    setSubmitError('')

    try {
      const response = await fetch(
        `http://localhost:8080/users/${userId}/categories/${selectedCategoryId}/expenses`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            description,
            amount: Number(amount),
            date,
          }),
        },
      )

      if (!response.ok) {
        throw new Error('Unable to add expense.')
      }

      const savedExpense = await response.json()

      setDescription('')
      setAmount('')
      setDate('')
      setSelectedCategoryId('')
      setExpenses(currentExpenses => [...currentExpenses, savedExpense])
    } catch (error) {
      console.error(error)
      setSubmitError('Could not add the expense. Please try again.')
    }
  }

  return (
    <main>
      <h1>SplitMate</h1>

      <section>
        <h2>Add Expense</h2>
        <form className="expense-form" onSubmit={handleSubmit}>
          <label>
            Description
            <input
              type="text"
              value={description}
              onChange={event => setDescription(event.target.value)}
              required
            />
          </label>

          <label>
            Amount
            <input
              type="number"
              min="0.01"
              step="0.01"
              value={amount}
              onChange={event => setAmount(event.target.value)}
              required
            />
          </label>

          <label>
            Date
            <input
              type="date"
              value={date}
              onChange={event => setDate(event.target.value)}
              required
            />
          </label>

          <label>
            Category
            <select
              value={selectedCategoryId}
              onChange={event => setSelectedCategoryId(event.target.value)}
              required
            >
              <option value="">Select a category</option>
              {categories.map(category => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>

          <button type="submit">Add Expense</button>
          {submitError && <p className="error-message">{submitError}</p>}
        </form>
      </section>

      <section>
        <h2>Expenses</h2>
        <div className="expense-list">
          {expenses.map(expense => (
            <article key={expense.id} className="expense-item">
              <p>{expense.description}</p>
              <p>${expense.amount}</p>
              <p>{expense.date}</p>
              <p>{expense.category?.name}</p>
            </article>
          ))}
        </div>
      </section>
    </main>
  )
}

export default App
