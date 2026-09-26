import { useEffect, useState } from 'react'

const userId = 2
const apiBaseUrl = 'http://localhost:8080'
const expensesUrl = `${apiBaseUrl}/users/${userId}/expenses`
const budgetsUrl = `${apiBaseUrl}/users/${userId}/budgets`
const spendingTotalUrl = `${expensesUrl}/total`
const currentDate = new Date()

async function fetchExpenses() {
  const response = await fetch(expensesUrl)

  if (!response.ok) {
    throw new Error('Unable to load expenses.')
  }

  return response.json()
}

async function fetchSpendingTotal() {
  const response = await fetch(spendingTotalUrl)

  if (!response.ok) {
    throw new Error('Unable to load total spending.')
  }

  return Number(await response.json())
}

function selectCurrentBudget(budgets) {
  const currentMonth = currentDate.getMonth() + 1
  const currentYear = currentDate.getFullYear()

  return (
    budgets.find(
      budget => budget.month === currentMonth && budget.year === currentYear,
    ) ?? budgets[0] ?? null
  )
}

function formatCurrency(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value)
}

function App() {
  const [expenses, setExpenses] = useState([])
  const [categories, setCategories] = useState([])
  const [description, setDescription] = useState('')
  const [amount, setAmount] = useState('')
  const [date, setDate] = useState('')
  const [selectedCategoryId, setSelectedCategoryId] = useState('')
  const [submitError, setSubmitError] = useState('')

  const [budget, setBudget] = useState(null)
  const [totalSpent, setTotalSpent] = useState(0)
  const [budgetAmount, setBudgetAmount] = useState('')
  const [budgetMonth, setBudgetMonth] = useState(
    String(currentDate.getMonth() + 1),
  )
  const [budgetYear, setBudgetYear] = useState(
    String(currentDate.getFullYear()),
  )
  const [budgetLoading, setBudgetLoading] = useState(true)
  const [budgetError, setBudgetError] = useState('')
  const [spendingError, setSpendingError] = useState('')
  const [budgetSubmitError, setBudgetSubmitError] = useState('')

  useEffect(() => {
    fetchExpenses()
      .then(setExpenses)
      .catch(error => console.error(error))

    fetch(`${apiBaseUrl}/categories`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Unable to load categories.')
        }

        return response.json()
      })
      .then(setCategories)
      .catch(error => console.error(error))

    fetch(budgetsUrl)
      .then(response => {
        if (!response.ok) {
          throw new Error('Unable to load budget.')
        }

        return response.json()
      })
      .then(budgets => setBudget(selectCurrentBudget(budgets)))
      .catch(error => {
        console.error(error)
        setBudgetError('Could not load the budget.')
      })
      .finally(() => setBudgetLoading(false))

    fetchSpendingTotal()
      .then(setTotalSpent)
      .catch(error => {
        console.error(error)
        setSpendingError('Could not load total spending.')
      })
  }, [])

  async function handleExpenseSubmit(event) {
    event.preventDefault()
    setSubmitError('')

    try {
      const response = await fetch(
        `${apiBaseUrl}/users/${userId}/categories/${selectedCategoryId}/expenses`,
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

      try {
        setTotalSpent(await fetchSpendingTotal())
        setSpendingError('')
      } catch (error) {
        console.error(error)
        setSpendingError('Could not refresh total spending.')
      }
    } catch (error) {
      console.error(error)
      setSubmitError('Could not add the expense. Please try again.')
    }
  }

  async function handleBudgetSubmit(event) {
    event.preventDefault()
    setBudgetSubmitError('')

    try {
      const response = await fetch(budgetsUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          amount: Number(budgetAmount),
          month: Number(budgetMonth),
          year: Number(budgetYear),
        }),
      })

      if (!response.ok) {
        throw new Error('Unable to create budget.')
      }

      setBudget(await response.json())
      setBudgetAmount('')
      setBudgetError('')
    } catch (error) {
      console.error(error)
      setBudgetSubmitError('Could not create the budget. Please try again.')
    }
  }

  const remainingBudget = budget
    ? Number(budget.amount) - totalSpent
    : 0

  return (
    <main>
      <h1>SplitMate</h1>

      <section>
        <h2>Budget Summary</h2>

        {budgetError && <p className="error-message">{budgetError}</p>}
        {spendingError && <p className="error-message">{spendingError}</p>}
        {budgetLoading && <p>Loading budget...</p>}

        {!budgetLoading && !budgetError && budget && (
          <div className="budget-summary">
            <p>
              <strong>Budget:</strong> {formatCurrency(Number(budget.amount))}
            </p>
            <p>
              <strong>Total spent:</strong>{' '}
              {spendingError ? 'Unavailable' : formatCurrency(totalSpent)}
            </p>
            <p>
              <strong>Remaining:</strong>{' '}
              {spendingError ? 'Unavailable' : formatCurrency(remainingBudget)}
            </p>
            <p>
              Budget period: {budget.month}/{budget.year}
            </p>
          </div>
        )}

        {!budgetLoading && !budgetError && !budget && (
          <div>
            <p>No budget set</p>
            <form className="expense-form" onSubmit={handleBudgetSubmit}>
              <label>
                Budget amount
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={budgetAmount}
                  onChange={event => setBudgetAmount(event.target.value)}
                  required
                />
              </label>

              <label>
                Month
                <input
                  type="number"
                  min="1"
                  max="12"
                  value={budgetMonth}
                  onChange={event => setBudgetMonth(event.target.value)}
                  required
                />
              </label>

              <label>
                Year
                <input
                  type="number"
                  min="2000"
                  value={budgetYear}
                  onChange={event => setBudgetYear(event.target.value)}
                  required
                />
              </label>

              <button type="submit">Create Budget</button>
              {budgetSubmitError && (
                <p className="error-message">{budgetSubmitError}</p>
              )}
            </form>
          </div>
        )}
      </section>

      <section>
        <h2>Add Expense</h2>
        <form className="expense-form" onSubmit={handleExpenseSubmit}>
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
