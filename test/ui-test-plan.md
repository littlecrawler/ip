# UI Test Plan

This plan checks Noah's observable command-line behavior. Each test case starts
with an empty task list in a fresh process. Expected output blocks correspond
one-to-one with the commands and must appear exactly in the same order.

The banner and separator lines are displayed in the transcript but are omitted
from expected blocks because they do not describe command behavior.

Run the complete plan from the repository root:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1
```

## Test cases

### Add and list all task types

Aim: Verify that todo, deadline, and event commands create correctly formatted
tasks and that `list` preserves their order.

### Mark and unmark an inherited task

Aim: Verify that status changes inherited from `Task` are reflected through
polymorphic deadline output.

### Accept tasks without date delimiters

Aim: Verify the intended fallback constructors when deadline or event timing
information is omitted.

### Reject empty todo and unknown commands

Aim: Verify that invalid commands show helpful errors and do not change the
task list.

### Reject invalid task numbers

Aim: Verify that mark and unmark commands reject missing, non-numeric, and
out-of-range task numbers without changing task state.

### Reject invalid deadline and event details

Aim: Verify that deadline and event commands reject empty descriptions and
incomplete date delimiters without changing the task list.

## Machine-readable cases

Keep this JSON block synchronized with the descriptions above. The
`test-ui` runner treats it as the executable source of truth.

<!-- TEST-CASES-START -->
```json
[
  {
    "name": "add-and-list-task-types",
    "aim": "Add and list todo, deadline, and event tasks in order.",
    "commands": [
      "todo borrow book",
      "deadline return book /by Sunday",
      "event project meeting /from Mon 2pm /to 4pm",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Got it. I've added this task:\n  [T][ ] borrow book\nNow you have 1 tasks in the list.",
      "Got it. I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 2 tasks in the list.",
      "Got it. I've added this task:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)\nNow you have 3 tasks in the list.",
      "Here are the tasks in your list:\n1.[T][ ] borrow book\n2.[D][ ] return book (by: Sunday)\n3.[E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  },
  {
    "name": "mark-and-unmark-deadline",
    "aim": "Update inherited completion state and preserve deadline details.",
    "commands": [
      "deadline return book /by Sunday",
      "mark 1",
      "unmark 1",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Got it. I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 1 tasks in the list.",
      "Nice! I've marked this task as done:\n  [D][X] return book (by: Sunday)",
      "OK, I've marked this task as not done yet:\n  [D][ ] return book (by: Sunday)",
      "Here are the tasks in your list:\n1.[D][ ] return book (by: Sunday)",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  },
  {
    "name": "tasks-without-date-delimiters",
    "aim": "Use fallback constructors when timing delimiters are absent.",
    "commands": [
      "deadline return book",
      "event project meeting",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "Got it. I've added this task:\n  [D][ ] return book\nNow you have 1 tasks in the list.",
      "Got it. I've added this task:\n  [E][ ] project meeting\nNow you have 2 tasks in the list.",
      "Here are the tasks in your list:\n1.[D][ ] return book\n2.[E][ ] project meeting",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  },
  {
    "name": "reject-empty-todo-and-unknown-command",
    "aim": "Reject invalid commands without changing the task list.",
    "commands": [
      "todo",
      "todo read book",
      "todoabc",
      "blah",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "A todo needs a description. Try: todo read book",
      "Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
      "I don't recognize that command. Please try again.",
      "I don't recognize that command. Please try again.",
      "Here are the tasks in your list:\n1.[T][ ] read book",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  },
  {
    "name": "reject-invalid-task-numbers",
    "aim": "Reject invalid mark and unmark task numbers without changing task state.",
    "commands": [
      "mark",
      "mark abc",
      "mark 1",
      "todo read book",
      "mark 2",
      "mark 1",
      "unmark 0",
      "unmark 1",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "A task number is required. Try: mark 1",
      "The task number must be a positive integer.",
      "There is no task numbered 1.",
      "Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
      "There is no task numbered 2.",
      "Nice! I've marked this task as done:\n  [T][X] read book",
      "The task number must be a positive integer.",
      "OK, I've marked this task as not done yet:\n  [T][ ] read book",
      "Here are the tasks in your list:\n1.[T][ ] read book",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  },
  {
    "name": "reject-invalid-deadline-and-event-details",
    "aim": "Reject incomplete deadline and event details without changing valid tasks.",
    "commands": [
      "deadline",
      "deadline /by Sunday",
      "deadline return book /by",
      "deadline return book",
      "event",
      "event /from Monday /to Tuesday",
      "event project meeting /from Monday",
      "event project meeting /to Tuesday",
      "event project meeting /from Monday /to",
      "event project meeting",
      "list",
      "bye"
    ],
    "expectedOutputs": [
      "A deadline needs a description. Try: deadline return book /by Sunday",
      "A deadline needs a description and date. Try: deadline return book /by Sunday",
      "A deadline needs a description and date. Try: deadline return book /by Sunday",
      "Got it. I've added this task:\n  [D][ ] return book\nNow you have 1 tasks in the list.",
      "An event needs a description. Try: event meeting /from 2pm /to 4pm",
      "An event needs a description, start, and end. Try: event meeting /from 2pm /to 4pm",
      "An event needs a description, start, and end. Try: event meeting /from 2pm /to 4pm",
      "An event needs a description, start, and end. Try: event meeting /from 2pm /to 4pm",
      "An event needs a description, start, and end. Try: event meeting /from 2pm /to 4pm",
      "Got it. I've added this task:\n  [E][ ] project meeting\nNow you have 2 tasks in the list.",
      "Here are the tasks in your list:\n1.[D][ ] return book\n2.[E][ ] project meeting",
      "Farewell, Traveler!\nHope to see you again soon."
    ]
  }
]
```
<!-- TEST-CASES-END -->
