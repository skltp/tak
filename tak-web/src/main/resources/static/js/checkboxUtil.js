$('.select-all').click(function(e){
    var checked = e.currentTarget.checked;
    $('.columnCheckbox').prop('checked', checked);
    countChecked((checked) ? 20 : 0);
});

var lastChecked = null;
$('.columnCheckbox').click(function(e){
    var selectAllChecked = $('.select-all:checked').length ? true : false;
    console.log(selectAllChecked)

    if (selectAllChecked) {
        var itemsTotal = $('.columnCheckbox').length;
        var uncheckedItemsTotal = itemsTotal - checkedItemsTotal();
        var selected = 20 - uncheckedItemsTotal;
        countChecked(selected);
    } else {
        countChecked();
    }

    if(!lastChecked) {
        lastChecked = this;
        return;
    }

    if(e.shiftKey) {
        var from = $('.columnCheckbox').index(this);
        var to = $('.columnCheckbox').index(lastChecked);

        var start = Math.min(from, to);
        var end = Math.max(from, to) + 1;

        $('.columnCheckbox').slice(start, end)
            .filter(':not(:disabled)')
            .prop('checked', lastChecked.checked);
        countChecked();
    }
    lastChecked = this;

    if(e.altKey){

        $('.columnCheckbox')
            .filter(':not(:disabled)')
            .each(function () {
                var $checkbox = $(this);
                $checkbox.prop('checked', !$checkbox.is(':checked'));
                countChecked();
            });
    }

});
function countChecked(number){
    number = number ? number : checkedItemsTotal();
    $('#counter-selected').html(number);
}

function checkedItemsTotal(){
    return $('.columnCheckbox:checked').length;
}