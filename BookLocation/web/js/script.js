$(document).ready(function () {
    var window_width = $(window).width();
    var tmp = $(document).attr("title").toLocaleLowerCase();
    //刷新导航栏active
    if (tmp == "home") {
        $("#home").addClass("active");
        //新闻自动滚动
        setInterval(function () {
            $("#new_" + lastclick).hide();
            if (window_width <= 768) {
                $("#des_" + lastclick).hide();
            }
            lastclick = (lastclick) % 4 + 1;
            $("#new_" + lastclick).show();
            if (window_width <= 768) {
                $("#des_" + lastclick).show();
            }
            $("#pic_des .card").removeClass("gradient-violat");
            $("#des_" + lastclick).addClass("gradient-violat");

        }, 3000)
    } else if (tmp == "people") {
        $(".navbar-nav li.active").removeClass("active");
        $("#people").addClass("active");
        $('.navbar').addClass('colored-nav');
        $('.navbar').addClass('gradient-violat');
    } else if (tmp == "publications") {
        $(".navbar-nav li.active").removeClass("active");
        $("#publications").addClass("active");
    } else if (tmp == "projects") {
        $(".navbar-nav li.active").removeClass("active");
        $("#projects").addClass("active");
    } else if (tmp == "about") {
        $(".navbar-nav li.active").removeClass("active");
        $("#about").addClass("active");
    }
    //初始化图片news展示
    $("#new_2").hide();
    $("#new_3").hide();
    $("#new_4").hide();
    $("#pic_des .card").removeClass("gradient-violat");
    if (window_width < 768) {
        $("#des_2").hide();
        $("#des_3").hide();
        $("#des_4").hide();
    }
    $("#des_1").addClass("gradient-violat");
    // OWL CAROUSEL INSTALLATION
    $("#testimonial-carousel").owlCarousel({
        items: 1,
        itemsDesktop: [1000, 1], //5 items between 1000px and 901px
        itemsDesktopSmall: [900, 1], // betweem 900px and 601px
        itemsTablet: [600, 1],
        itemsMobile: [479, 1],
        pagination: true
    });
    $("#home-slider").owlCarousel({
        items: 1,
        itemsDesktop: [1000, 1], //5 items between 1000px and 901px
        itemsDesktopSmall: [900, 1], // betweem 900px and 601px
        itemsTablet: [600, 1],
        itemsMobile: [479, 1],
        pagination: false,
        navigation: true,
        navigationText: ["<i class='ion-ios-arrow-left'></i>", "<i class='ion-ios-arrow-right'></i>"]

    });

    /* Navigation Menu*/
    var offsettop = $('.navbar').offset().top;

    if (offsettop > 50) {
        $('.navbar').addClass('colored-nav');
        $('.navbar').addClass('gradient-violat');
        $("#scroll-top-div").fadeIn('500');
    } else {
        $('.navbar').removeClass('colored-nav');
        $('.navbar').removeClass('gradient-violat');
        $("#scroll-top-div").fadeOut('500');
    }
    var num = 50; //number of pixels before modifying styles

    $(window).bind('scroll', function () {
        if ($(window).scrollTop() > num) {
            $('.navbar').addClass('colored-nav');
            $('.navbar').addClass('gradient-violat');
            $("#scroll-top-div").fadeIn('500');
        } else {
            $('.navbar').removeClass('colored-nav');
            $('.navbar').removeClass('gradient-violat');
            $("#scroll-top-div").fadeOut('500');
        }
    });
    //点击侧边切换新闻
    var lastclick = 1; //上一次点击的新闻
    $("#pic_des .card").on('click', function (event) {
        var tmp = $(this).attr("id");

        if (tmp == "des_1") {
            $("#new_" + lastclick).hide();
            lastclick = 1;
            $("#new_1").show();
            $("#pic_des .card").removeClass("gradient-violat");
            $("#des_1").addClass("gradient-violat");

        } else if (tmp == "des_2") {
            $("#new_" + lastclick).hide();
            lastclick = 2;
            $("#new_2").show();
            $("#pic_des .card").removeClass("gradient-violat");
            $("#des_2").addClass("gradient-violat");
        } else if (tmp == "des_3") {
            $("#new_" + lastclick).hide();
            lastclick = 3;
            $("#new_3").show();
            $("#pic_des .card").removeClass("gradient-violat");
            $("#des_3").addClass("gradient-violat");
        } else if (tmp == "des_4") {
            $("#new_" + lastclick).hide();
            lastclick = 4;
            $("#new_4").show();
            $("#pic_des .card").removeClass("gradient-violat");
            $("#des_4").addClass("gradient-violat");
        }
    });
    //点击图片切换新闻
    $("#pic_news").on('click', function (event) {
        $("#new_" + lastclick).hide();
        lastclick = (lastclick) % 4 + 1;
        $("#new_" + lastclick).show();
        $("#pic_des .card").removeClass("gradient-violat");
        $("#des_" + lastclick).addClass("gradient-violat");

    });
    var count = 0;
    //点击合上导航栏
    $(".navbar-toggle").on('click', function (event) {
        $('.navbar').addClass('gradient-violat');
        $(".navbar-toggle").addClass("collapsed");
        if (count == 1) {
            window.location.reload();
        }
        count++;

    });


    /* SMOOTH SCROLLING SCRIPT*/
    // Add smooth scrolling to all links
    $(".navbar-nav li a").on('click', function (event) {

        // Make sure this.hash has a value before overriding default behavior
        if (this.hash !== "") {
            // Prevent default anchor click behavior
            event.preventDefault();
            // Store hash
            var hash = this.hash;
            // Using jQuery's animate() method to add smooth page scroll
            // The optional number (800) specifies the number of milliseconds it takes to scroll to the specified area
            $('html, body').animate({
                scrollTop: $(hash).offset().top
            }, 800, function () {

                // Add hash (#) to URL when done scrolling (default click behavior)
                window.location.hash = hash;
            });
        } // End if
    });

    /****************************BACK TO TOP************************************/
    $('#scroll-top-div').on('click', function (e) {
        e.preventDefault();
        $('html,body').animate({
            scrollTop: 0
        }, 700);
    });

});